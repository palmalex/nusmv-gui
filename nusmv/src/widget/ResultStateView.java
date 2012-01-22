package widget;

import java.util.Vector;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QMatrix;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QRadialGradient;
import com.trolltech.qt.gui.QStyle;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWheelEvent;
import com.trolltech.qt.gui.QWidget;

public class ResultStateView extends QGraphicsView {
	private int stateNumber;
	private int loop;
	private int currentNode;
	private int timerId;
	private Vector<Node> nodes = new Vector<Node>();
	
	
	private static final QPen QPEN_BLACK = new QPen(QColor.black, 0);
	private static final QPen QPEN_EDGE = new QPen(QColor.black, 1, Qt.PenStyle.SolidLine, Qt.PenCapStyle.RoundCap, Qt.PenJoinStyle.RoundJoin);
	private static QRadialGradient GRADIENT_SUNKEN;
	private static QRadialGradient GRADIENT_NORMAL;
	private static QRadialGradient CURRENT_NODE_NORMAL;
	private static QRadialGradient CURRENT_NODE_SUNKEN;
	private static QPainterPath NODE_SHAPE;
	
	public Signal0 STATECHANGE = new Signal0();
	
	

	static {
		NODE_SHAPE = new QPainterPath();
		NODE_SHAPE.addEllipse(-10, -10, 20, 20);
		
		GRADIENT_SUNKEN = new QRadialGradient(-3, -3, 10);
		GRADIENT_SUNKEN.setCenter(3, 3);
		GRADIENT_SUNKEN.setFocalPoint(3, 3);
		GRADIENT_SUNKEN.setColorAt(1, new QColor(QColor.yellow).lighter(120));
		GRADIENT_SUNKEN.setColorAt(0, new QColor(QColor.darkYellow).lighter(120));
		
		CURRENT_NODE_SUNKEN = new QRadialGradient(-3, -3, 10);
		CURRENT_NODE_SUNKEN.setCenter(3, 3);
		CURRENT_NODE_SUNKEN.setFocalPoint(3, 3);
		CURRENT_NODE_SUNKEN.setColorAt(1, new QColor(QColor.red).lighter(120));
		CURRENT_NODE_SUNKEN.setColorAt(0, new QColor(QColor.darkRed).lighter(120));
		
		GRADIENT_NORMAL = new QRadialGradient(-3, -3, 10);
		GRADIENT_NORMAL.setColorAt(0, QColor.yellow);
		GRADIENT_NORMAL.setColorAt(1, QColor.darkYellow);
		
		CURRENT_NODE_NORMAL = new QRadialGradient(-3, -3, 10);
		CURRENT_NODE_NORMAL.setColorAt(0, QColor.red);
		CURRENT_NODE_NORMAL.setColorAt(1, QColor.darkRed);
		
	}
	
	public ResultStateView(int stateNumber,int loop){
		QGraphicsScene scene = new QGraphicsScene(this);
		scene.setItemIndexMethod(QGraphicsScene.ItemIndexMethod.NoIndex);
		
		setScene(scene);
		setCacheMode(new QGraphicsView.CacheMode(QGraphicsView.CacheModeFlag.CacheBackground));
		setRenderHint(QPainter.RenderHint.Antialiasing);
		setTransformationAnchor(QGraphicsView.ViewportAnchor.AnchorUnderMouse);
		setResizeAnchor(QGraphicsView.ViewportAnchor.AnchorViewCenter);
		currentNode = 0;
		this.stateNumber = stateNumber;
		this.loop=loop;
		
		int secondRow = (stateNumber-loop)/2;
		int max = loop!=0?loop+secondRow:stateNumber;
		
		scene.setSceneRect(-50,-50,200,100+max*50);
		Node node=null;
		Node prevNode=null;
		Edge edge;
		for(int i=0; i<max; i++) {
			node = new Node(this,i+1);
			nodes.add(node);
			node.setPos(0, i*50);
			node.setToolTip("Status "  + String.valueOf(i+1));
			scene.addItem(node);
			if (prevNode!=null) {
				edge = new Edge(prevNode,node);
				scene.addItem(edge);
			}
			prevNode=node;
		}
		
		if(loop!=0){
			for(int i=max; i<stateNumber; i++) {
				node = new Node(this,i+1);
				nodes.add(node);
				node.setPos(100, (max-1)*50 - (i-max)*50);
				node.setToolTip("Status " + String.valueOf(i+1));
				scene.addItem(node);
				if(prevNode!=null) {
					edge = new Edge(prevNode,node);
					scene.addItem(edge);
				}
				prevNode=node;
			}
			edge=new Edge(node,nodes.elementAt(loop-1));
			scene.addItem(edge);		
		}
		setCurrentNode(1);
	
		
		scale(0.8, 0.8);
	}
	
	protected void wheelEvent(QWheelEvent event) {
		System.out.println("wheel" + Math.pow(2, -event.delta()/240));
		scaleView(Math.pow(2, -event.delta()/240.00));
	}
	
	public void setCurrentNode(int currentNode) {
		Node oldCurrent = nodes.get(this.currentNode-1>0?this.currentNode-1:0);
		oldCurrent.setCurrent(false);
		Node current = nodes.get(currentNode-1);
		current.setCurrent(true);
		this.currentNode=currentNode;
		update();
		STATECHANGE.emit();
	}
	
	public int getCurrentNode() {
		return currentNode;
	}
	
	public boolean hasNext() {
		boolean ret = true;
		if (currentNode==stateNumber & loop==0) {
			ret = false;
		}
		return ret;
	}
	
	public boolean hasPrev() {
		return currentNode==1;
	}
	
	public void next() {
		if (currentNode<stateNumber) {
			setCurrentNode(1+currentNode);
		} else {
			if (loop!=0) {
				setCurrentNode(loop);
			}
		}
	}
	
	public void prev() {
		if (currentNode>1) {
			setCurrentNode(currentNode-1);
		}
	}
	
	
	private void scaleView(double scaleFactor) {
		QMatrix m = matrix();
		m.scale(scaleFactor, scaleFactor);
		double factor = m.mapRect(new QRectF(0, 0, 1, 1)).width();
		
		System.out.println("factor " + factor);
		if (factor < 0.07 || factor > 100)
			return;
		
		scale(scaleFactor,scaleFactor);
	}

	private void itemMoved(){
		if (timerId == 0) {
			timerId = startTimer(1000 / 25);
		}
	}
	
	public class Node extends QGraphicsItem {
		

		private Vector<Edge> edgeList = new Vector<Edge>();
		private ResultStateView graph;
		private QPointF newPos;
		private double adjust = 2;
		private QRectF boundingRect = new QRectF(-10 - adjust, -10 - adjust, 23 + adjust, 23 + adjust);
		private boolean current=false;
		int nodeNumber;
		
		Node(ResultStateView graphWidget, int nodeNumber) {
			graph = graphWidget;
			this.nodeNumber = nodeNumber;
			setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable);
			setZValue(1);
			newPos = pos();
		}
		
		private void addEdge(Edge edge) {
			edgeList.add(edge);
			edge.adjust();
		}
		@Override
		public QRectF boundingRect() {
			return boundingRect;
		}

		public void setCurrent(boolean set) {
			this.current = set;
			update();
		}
		
		@Override
		public void paint(QPainter painter, QStyleOptionGraphicsItem option,
				QWidget widget) {
			painter.setPen(Qt.PenStyle.NoPen);
			painter.setBrush(QColor.fromRgba(QColor.black.rgb() & 0x7fffffff));
			painter.drawEllipse(-7, -7, 20, 20);
			
			
			if ((option.state().isSet(QStyle.StateFlag.State_Sunken))) {
				if(!current) {
					painter.setBrush(GRADIENT_SUNKEN);
				} else {
					painter.setBrush(CURRENT_NODE_SUNKEN);
				}
			} else {
				if (!current) {
					painter.setBrush(GRADIENT_NORMAL);
				} else {
					painter.setBrush(CURRENT_NODE_NORMAL);
				}
			}
			
			
			painter.setPen(QPEN_BLACK);
			painter.drawEllipse(-10, -10, 20, 20);
		}
		
		public Object itemChange(GraphicsItemChange change, Object value) {
			switch (change) {
			case ItemPositionChange:
					for (Edge edge : edgeList){
						edge.adjust();
					}
					graph.itemMoved();
					break;
			default:
				break;
			}
			
			return super.itemChange(change, value);
			
		}
		
		public QPainterPath shape() {
			return NODE_SHAPE;
		}
		
		public void mousePressEvent(QGraphicsSceneMouseEvent event) {
			update();
			super.mousePressEvent(event);
		}
		
		public void mouseReleaseEvent(QGraphicsSceneMouseEvent event) {
			update();
			super.mouseReleaseEvent(event);
		}
		
		
		public void mouseDoubleClickEvent(QGraphicsSceneMouseEvent event) {
			// TODO trigger signal to send the selected status
			System.out.println("double click");
			graph.setCurrentNode(nodeNumber);
			super.mouseDoubleClickEvent(event);
			
		}
		
		
		
	}
	
	public class Edge extends QGraphicsItem {
		private Node source;
		private Node dest;
		
		private QPointF sourcePoint = new QPointF();
		private QPointF destPoint = new QPointF();
		private double arrowSize = 10;
		private double penWidth =1 ;
		private double extra = (penWidth + arrowSize) / 2.0;
		
		private QRectF boundingRect = new QRectF();
		
		QPointF destArrowP1 = new QPointF();
		QPointF destArrowP2 = new QPointF();
		
		QPolygonF pol1 = new QPolygonF();
		public Edge(Node sourceNode, Node destNode) {
			source = sourceNode;
			dest = destNode;
			source.addEdge(this);
			dest.addEdge(this);
			adjust();
		}
		
		private Node sourceNode() {
			return source;
		}
		
		private Node destNode() {
			return dest;
		}
		@Override
		public QRectF boundingRect() {
			// TODO Auto-generated method stub
			return boundingRect;
		}

		
		public void adjust(){
			double dx = source.pos().x() - dest.pos().x();
			double dy = source.pos().y() - dest.pos().y();
			
			double length = Math.sqrt(dx*dx+dy*dy);
			if (length == 0) return;
			
			double paddingX = dx/length*10;
			double paddingY = dy/length*10;
			
			prepareGeometryChange();
			sourcePoint.setX(source.pos().x() - paddingX);
			sourcePoint.setY(source.pos().y() - paddingY);
			
			destPoint.setX(dest.pos().x() + paddingX);
			destPoint.setY(dest.pos().y() + paddingY);
			
			boundingRect.setBottomLeft(source.pos());
			boundingRect.setTopRight(dest.pos());
			
			boundingRect = boundingRect.normalized();
			boundingRect.adjust(-extra, -extra, extra, extra);
		}
		
		public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) {
			if (source == null || dest == null) 
				return;
			
			QLineF line = new QLineF(sourcePoint, destPoint);
			
			painter.setPen(QPEN_EDGE);
			painter.drawLine(line);
			
			double angle;
			if (line.length() > 0)
				angle = Math.acos(line.dx() / line.length());
			else 
				angle = 0;
			
			if (line.dy() >=0)
				angle = (Math.PI * 2) - angle;
			
			destArrowP1.setX(destPoint.x() + Math.sin(angle - Math.PI/3) * arrowSize);
			destArrowP1.setY(destPoint.y() + Math.cos(angle - Math.PI/3) * arrowSize);
			destArrowP2.setX(destPoint.x() + Math.sin(angle - Math.PI + Math.PI/3) * arrowSize);
			destArrowP2.setY(destPoint.y() + Math.cos(angle - Math.PI + Math.PI/3) * arrowSize);
			
			pol1.clear();
			pol1.append(line.p2());
			pol1.append(destArrowP1);
			pol1.append(destArrowP2);
			
			painter.setBrush(QColor.black);
			painter.drawPolygon(pol1);
			
			
			
		}
		
	}
}

