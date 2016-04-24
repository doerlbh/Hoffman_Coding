// Baihan Lin, 1360521, Section AP, June 2014
// Prof: Helene Martin, TA: Autumn Johnson
// CSE 143, HW 8: Huffman Coding

// About: HuffmanTree is a class with functions to generate a binary tree as a 
// coding system to compress text based on Huffman Coding. The functions include
// generate the tree from scanner input or int array, decode and write into file.

import java.io.*;
import java.util.*;

public class HuffmanTree {

	private HuffmanNode overallRoot;

	// Construct a Huffman tree with leaves as encoded characters and branches as 
	// keys of coding. The more frequent character is, the shorter branches it have.
	// Pre: coding system. Throws an IllegalArgumentException if input array is null.
	public HuffmanTree(int[] counts) {
		checkNull(counts);
		Queue<HuffmanNode> charArray = new PriorityQueue<HuffmanNode>();
		for (int i = 0; i < 256; i++)	{
			int occur = counts[i];
			if (occur != 0) {
				charArray.add(new HuffmanNode(occur, i));
			}
		}
		charArray.add(new HuffmanNode(1, counts.length));
		buildTreeFromArray(charArray);
	}

	// Helper method. Pre: input a priority queue of nodes storing frequency of each character
	// Post: build the tree from the coding information given set the leaves as each character
	private void buildTreeFromArray(Queue<HuffmanNode> charArray) {
		if (charArray.size() == 1) {
			overallRoot = charArray.remove();
		} else {
			HuffmanNode small1 = charArray.remove();
			HuffmanNode small2 = charArray.remove();
			int freqSum = small1.freq + small2.freq;
			charArray.add(new HuffmanNode(freqSum, -1, small1, small2));
			buildTreeFromArray(charArray);
		}
	}

	// Construct a Huffman tree with leaves as encoded characters and branches as 
	// keys of coding.
	// Pre: coding system. Throws an IllegalArgumentException if scanner input is null.
	public HuffmanTree(Scanner input) {
		checkNull(input);
		overallRoot = new HuffmanNode();
		while (input.hasNextLine()) {
			int charAscii = Integer.parseInt(input.nextLine());
			String code = input.nextLine();
			HuffmanNode current = overallRoot;
			for (int i = 0; i < code.length(); i++) {
				int bit = Integer.parseInt("" + code.charAt(i)); //check encoded digit one by one
				if (bit == 0) {
					if (current.left == null) {
						current.left = new HuffmanNode();
					}
					current = current.left;
				} else if (bit == 1) {
					if (current.right == null) {
						current.right = new HuffmanNode();
					}
					current = current.right;
				}
			}
			current.ascii = charAscii;         // to the leave node, ready to set ASCII info
		}
	}

	// Pre: input a printstream, throws IllegalArgumentExceptio if null
	// Post: output the coding information in a pre-order traversal from the tree.
	public void write(PrintStream output) {
		checkNull(output);
		write(output, overallRoot, "");
	}

	// helper method for write. write the coding information into output
	private void write(PrintStream output, HuffmanNode root, String soFar) {
		if (root.isLeaf()) {
			output.println(root.ascii);
			output.println(soFar);
		} else {
			write(output, root.left, soFar + "0");
			write(output, root.right, soFar + "1");
		}
	}

	// Pre: given input and output, throws IllegalArgumentException if null
	// Post: decode the encoded input and output the decoded information, stop
	// when reaching end of file (EOF) signal.
	public void decode(BitInputStream input, PrintStream output, int eof) {
		checkNull(input);
		checkNull(output);
		int bit = input.readBit();
		String result = "";
		HuffmanNode current = overallRoot;
		while (bit != -1) {
			if (current.isLeaf()) {
				int asciiNow = current.ascii;
				if (asciiNow == eof) {
					bit = -1;                  // stop the loop
				} else {
					result += (char) asciiNow;
					current = overallRoot;     // reset the pointer, start on new character
				}     
			} else {
				if(bit == 0) {
					current = current.left;
				} else if (bit == 1) {
					current = current.right;
				}
				bit = input.readBit();          // update bit
			}
		}
		output.println(result);
	}

	// Pre: given an object as an input
	// Post: if it is null, throw an IllegalArgumentException
	private void checkNull(Object o) {
		if (o == null) {
			throw new IllegalArgumentException();
		}
	}

	// inner class for HuffmanTree. Define the properties of HuffmanNode.
	private class HuffmanNode implements Comparable<HuffmanNode> {
		public int freq; // freq of the character occuring in file
		public int ascii; // the ascii value of the character stored in that node
		public HuffmanNode left; // reference to left subtree
		public HuffmanNode right; // reference to right subtree

		// Construct a default leaf HuffmanNode with ascii and freq both -1
		public HuffmanNode() {
			this(-1, -1);
		}

		// Constructs a leaf node with the given frequency and ascii combination.
		public HuffmanNode(int freq, int ascii) {
			this(freq, ascii, null, null);
		}

		// Constructs a leaf or branch node with the given freq, ascii and links.
		public HuffmanNode(int freq, int ascii, HuffmanNode left, HuffmanNode right) {
			this.freq = freq;
			this.ascii = ascii;
			this.left = left;
			this.right = right;
		}

		// implements the compareTo method based only on frequency.
		public int compareTo(HuffmanNode other) {
			return freq - other.freq;
		}

		// Returns true is node is a leaf and false otherwise
		public boolean isLeaf() {
			return left == null && right == null;  
		}

	}

}
