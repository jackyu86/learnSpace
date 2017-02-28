package demo.structures.algorithm.tree;


public class DemoBinaryTree {
	
	DemoBinaryNode root;
	
	
	
	//binary insert
	
	public void insert(Comparable  element){
		root = insert(element,root);
	}
	
	private DemoBinaryNode insert(Comparable element,DemoBinaryNode t){
		if(element==null){
			return null;
		}
		//如果所属根为空创建节点
		if(t==null){
			t = new DemoBinaryNode(element,null,null);
		}
		//left
		else if(element.compareTo(t.getElement())<0){
			t.leftNode=insert(element,t.leftNode);
		}
		//right
		else if(element.compareTo(t.getElement())>0){
			t.rightNode=insert(element,t.rightNode);
		}
		else{
			
		}
		return t;
	}
    public void printTree( )
    {
        if( isEmpty( ) )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }
    
    public boolean isEmpty( )
    {
        return root == null;
    }

	
	private void printTree(DemoBinaryNode t){
		 if( t != null )
         {
             printTree( t.leftNode );
             System.out.println( t.element );
             printTree( t.rightNode );
         }
	}
	
	public static void main(String[] args) {
		
		   DemoBinaryTree t = new DemoBinaryTree( );
           final int NUMS = 4000;
           final int GAP  =   37;
           for( int i = GAP; i != 0; i = ( i + GAP ) % NUMS )
              t.insert(new Integer(i));
           
           t.printTree();
		
	}

}
