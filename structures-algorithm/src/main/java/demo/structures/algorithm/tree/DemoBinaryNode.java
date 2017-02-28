package demo.structures.algorithm.tree;

public class DemoBinaryNode {
	
	//元素对象
	 Comparable element;
	//左节点句柄
	 DemoBinaryNode leftNode;
	//右节点句柄
	 DemoBinaryNode rightNode;
	public Comparable getElement() {
		return element;
	}
	public void setElement(Comparable element) {
		this.element = element;
	}
	public DemoBinaryNode getLeftNode() {
		return leftNode;
	}
	public void setLeftNode(DemoBinaryNode leftNode) {
		this.leftNode = leftNode;
	}
	public DemoBinaryNode getRightNode() {
		return rightNode;
	}
	public void setRightNode(DemoBinaryNode rightNode) {
		this.rightNode = rightNode;
	}
	public DemoBinaryNode(Comparable element, DemoBinaryNode leftNode,
			DemoBinaryNode rightNode) {
		super();
		this.element = element;
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	
}
