package org.trackexplorer.parts.overview.tracklist.filehierarchy;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * A generic class representing a tree.
 */
public class Tree<T> {
    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>(rootData);
    }
    
    public Node<T> getRoot() {
    	return root;
    }
    
    /**
     * Private class representing a node of a tree.
     * 
     * Nodes are not constant and can be altered during runtime (e.g. add child)
     */
    public static class Node<T> {
        private final T data;        
        private List<Node<T>> children;
        
        public Node(final T nodeData) {
        	data = nodeData;
        	children = null;
        }
        
        public boolean hasChildren() {
        	if(children != null)
        	{
        		return children.size() != 0;
        	}
        	else
        	{
        		return false;
        	}
        }
        
        public List<Node<T>> getChildren() {
        	if(hasChildren())
        	{
        		return Collections.unmodifiableList(children);
        	}
        	else
        	{
        		return new ArrayList<Node<T>>();
        	}
        }
        
        public void addChild(Node<T> child) {
        	if(children == null) {
        		children = new ArrayList<Node<T>>();
        	}
        	children.add(child);
        }
        
        public void addChildren(List<Node<T>> children) {
        	if(this.children == null) {
        		this.children = new ArrayList<Node<T>>();
        	}
        	if(children != null) {
        		this.children.addAll(children);
        	}
        }
        
        public void merge(Node<T> node) {
        	addChildren(node.children);
        }
        
        public T getData() {
        	return data;
        }
        
        public int hashCode()
        {
        	return data.hashCode();
        }

        public boolean equals (Object obj)
        {
        	return (obj != null) &&
        			(obj instanceof Node<?>) &&
        			((Node<?>)obj).data.equals(data);
        }
    }
}