package Model;

import java.io.FileWriter;
import java.io.IOException;

public class DotBuilder
{
	private FileWriter output;
	private int id;
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
		id = 0;
		startingPlayer= player;
	}

	public void addNode(Move node)
	{
		if(node.id == 0)
			node.id = ++id;
	}

	public void addEdge(Move father, Move child)
	{
		addNode(father);
		addNode(child);
		try
		{
			output.write(father.id + " -> " + child.id + "\n");
		} catch (IOException e)
		{
			return;
		}
	}

	public void changeColor(Move node, String color)
	{
		addNode(node);
		try
		{
			output.write(node.id + " [color = " + color + ", style = filled ]\n");
		} catch (IOException e)
		{
			return;
		}
	}

	public void setLabel(Move node)
	{
		addNode(node);
		try
		{
			if(node.start)
				output.write(node.id + " [label = \"" + getLabel(node) +"\", shape = " + getShape(node) + ",color = red, style = filled ]\n");
			else
				output.write(getOutput(node));
		} catch (IOException e)
		{
			return;
		}
	}

	public String getOutput(Move node)
	{
		String out = node.id + " [label =  \"" + getLabel(node) + "\" , shape = " + getShape(node);
		if(node.pruned)
			out += ",color = grey, style = filled";
		out += "]\n";
		return out;
	}

	private String getLabel(Move node)
	{
		String ret = node.toString();
		if(!node.pruned)
			ret+= " " + node.value;
		return ret;
	}

	private String getShape(Move node)
	{
		return node.player != startingPlayer? "rectangle" : "ellipse";
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
