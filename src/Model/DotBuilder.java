package Model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DotBuilder
{
	private Map<Node, Integer> nodes;
	private FileWriter output;
	private int size;
	private int startingPlayer;

	public DotBuilder(int player)
	{
		try
		{
			output = new FileWriter("tree.dot");
			output.write("digraph {\n");
		} catch (IOException e)
		{
			output = null;
		}
		size = 0;
		nodes = new HashMap<>();
		startingPlayer= player;
	}
	
	public void addNode(Node node)
	{
		if(!nodes.containsKey(node))
			nodes.put(node, ++size);
	}
	
	public void addEdge(Node father, Node child)
	{
		addNode(father);
		addNode(child);
		try
		{
			output.write(nodes.get(father) + " -> " + nodes.get(child) + "\n");
		} catch (IOException e)
		{
			return;
		}
	}
	
	public void changeColor(Node node, String color)
	{
		addNode(node);
		try
		{
			output.write(nodes.get(node) + " [color = " + color + ", style = filled ]\n");
		} catch (IOException e)
		{
			return;
		}
	}
	
	public void setLabel(Node node)
	{
		addNode(node);
		try
		{
			if(node.getxPos() == -2)
				output.write(nodes.get(node) + " [label = \"" + getLabel(node) +"\", shape = " + getShape(node) + ",color = red, style = filled ]\n");
			else
				output.write(getOutput(node));
		} catch (IOException e)
		{
			return;
		}
	}
	
	public String getOutput(Node node)
	{
		String out = nodes.get(node) + " [label =  \"" + getLabel(node) + "\" , shape = " + getShape(node);
		if(node.getColor() != 0)
			out += ",color = " + getColor(node) + ", style = filled";
		out += "]\n";
		return out;	
	}
	
	private String getLabel(Node node)
	{
		String ret = node.toString();
		if(node.getColor() != 2)
			ret+= " " + node.getHeuristicValue();
		return ret;
	}
	
	private String getColor(Node node)
	{
		switch(node.getColor()){
			case 1:
				return "red";
			case 2:
				return "grey";
			default:
				return "white";
		}
	}
	
	private String getShape(Node node)
	{
		return node.getPlayer() != startingPlayer? "rectangle" : "ellipse";
	}

	public void close()
	{
		try
		{
			output.write("}");
			output.close();
		} catch (IOException e)
		{
			return;
		}
	}
}
