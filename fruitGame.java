import java.io.*;
import java.util.*;

public class fruitGame {
	
	public static long StartTime;
	public static int DepthLimit = 3; // default;
	public static long Urgent = (long)3.0;
	public static float Time_Limit;
	public static boolean Emergency = false;
	public static int NumOfStar = 0;

	public static class Position
	{
		int row, col;
		char value; // from board
		int val; // track value
		
		Position()
		{}
		
		Position (int row, int col)
		{
			this.row = row; this.col = col;
		}
	}
	
	public static class Node
	{
		boolean v = false;
		Position pos;
		int depth;
		int myScore, value;
		int alpha, beta;
		char[][] myBoard;
		Stack<Position> mychildren;
		Position nextMove_pos;
		
		Node()
		{}
		
		Node (Position pos)
		{
			this.pos = pos;
			myScore=0; depth = 0;
			alpha = Integer.MIN_VALUE; beta = Integer.MAX_VALUE; value = Integer.MIN_VALUE;
			mychildren = new Stack<Position>();
			nextMove_pos = new Position();
		}
	}
	
	public static char[][] updateBoard_star(char[][] board,int N)
	{
		int star_index = -1;
		boolean found_star = false;
		
		for (int col = 0; col < N; col++)
		{
			found_star = false;
			star_index = -1;
			for (int row = N-1; row >= 0; row--)
			{
				if (!found_star && board[row][col] == '*')
				{// first star 
					found_star = true;
					star_index = row;
				}
			
				if (found_star && board[row][col] != '*')
				{
					board[star_index][col] = board[row][col];
					board[row][col] = '*';
					star_index --;
				}
			}
		}
		
		return board;
	}
	
	public static Stack<Position> makeChildrenStack(Node n, int N)
	{
		char poped_val;
		int poped_row, poped_col;
		Position poped_pos;
		boolean[][] visited = new boolean[N][N]; // false
		Stack<Position> childPos = new Stack<Position>();
		Queue<Position> myque = new LinkedList<Position>();
		
		for (int row = 0; row < N; row++)
		{
			for (int col = 0; col < N; col++)
			{
				if (visited[row][col] || n.myBoard[row][col] == '*')
				{
					continue;
				}
				else
				{
					Position first_pos = new Position(row, col);
					first_pos.value = n.myBoard[row][col];
					myque.add(first_pos);
					visited[row][col] = true;

					while (!myque.isEmpty())
					{
						poped_pos = myque.poll();
						poped_row = poped_pos.row;
						poped_col = poped_pos.col;
						poped_val = n.myBoard[poped_row][poped_col];
								
						// Down
						if ((poped_row < N-1)
								&& poped_val == n.myBoard[poped_row+1][poped_col] 
								&& !visited[poped_row+1][poped_col])
						{
							//System.out.println("down");
							myque.add(new Position(poped_row+1, poped_col));
							visited[poped_row+1][poped_col] = true;
						}
						
						// Up
						if ((poped_row > 0)
								&& poped_val == n.myBoard[poped_row-1][poped_col] 
								&& !visited[poped_row-1][poped_col])
						{
							//System.out.println("up");
							myque.add(new Position(poped_row-1, poped_col));
							visited[poped_row-1][poped_col] = true;
						}
							
						// Left
						if ((poped_col > 0)
								&& poped_val == n.myBoard[poped_row][poped_col-1] 
								&& !visited[poped_row][poped_col-1])
						{
							//System.out.println("left");
							myque.add(new Position(poped_row, poped_col-1));
							visited[poped_row][poped_col-1] = true;
						}
						
						// Right
						if ((poped_col < N-1)
								&& poped_val == n.myBoard[poped_row][poped_col+1]  
								&& !visited[poped_row][poped_col+1])
						{
							myque.add(new Position(poped_row, poped_col+1));
							visited[poped_row][poped_col+1] = true;
						}
					}
					
					// add to children stack
					childPos.push(first_pos);
					
				}//else:!visitied
				
			}//for:col
		}//for:row
		
		return childPos;
	}
			
	public static Position decideNextMove(int N, int P, char[][] board)
	{		
		char poped_value;
		int score = 0;
		int rootChildSize = 0;
		int poped_row, 	poped_col;
		boolean[][] visited = new boolean[N][N]; // false
		Node cur_node = new Node();
		Position poped_pos;
		Position nextChild_pos;
		Node returnValue = new Node(); // backTacking node
		returnValue.alpha = Integer.MIN_VALUE;
		returnValue.beta = Integer.MAX_VALUE;
		Stack<Node> mystack = new Stack<Node>();
		Queue<Position> myque = new LinkedList<Position>();
		
		// 1. Set root node
		Node root = new Node(new Position(-1,-1));
		root.myBoard = new char[N][N];
		for(int i = 0; i < N; i ++)
		{
			for (int j = 0; j < N; j++)
			{
				if (board[i][j] == '*')
				{
					NumOfStar++;
				}
				root.myBoard[i][j] = board[i][j];
			}
		}
		
		// 2. make first children of root
		root.mychildren = makeChildrenStack(root, N);
		System.out.println("Root's children number is " + root.mychildren.size());
		mystack.push(root);
		
		//* Set DepthLimit
		if (!Emergency)
		{
			rootChildSize = root.mychildren.size();
			int almostEnd = (3*N*N)/4;
			
            if (N > 20)
            {// 21~26
            	if (rootChildSize <= 80)
            	{
            		DepthLimit = 3;
            	}
            	else
            	{
            		if (P < 3 || NumOfStar > almostEnd) // Check!
            		{
            			DepthLimit = 3;
            		}
            		else
            		{
            			DepthLimit = 2;
            		}
            	}
            }
            else if (N > 15)
            {// 16 ~20
            	if (rootChildSize <= 120)
            	{
            		DepthLimit = 3;
            	}
            	else
            	{
            		if (P < 3 || NumOfStar > almostEnd)
            		{
            			DepthLimit = 3;
            		}
            		else
            		{
            			DepthLimit = 2;
            		}
            	}
            }
            else if (N > 10)
            {//11 ~15
            	if (rootChildSize <= 80)
            	{
            		DepthLimit = 4;
            	}
            	else
            	{
            		DepthLimit = 3;
            	}
            }
            else if (N > 7)
            {// 8~10
            	DepthLimit = 4;
            }
            else
            {// 1~7
            	DepthLimit = 5;
            }
            System.out.println("DepthLimit: " + DepthLimit);
		}
		
		// 3. Start DFS from depth0(root)
		long current_time;
		while (!mystack.isEmpty())
		{
			current_time = (long) (Time_Limit - (( System.currentTimeMillis() - StartTime) / 1000F ));
			if ( current_time < Urgent)
			{
				System.out.println("NO TIME! - Greedy Answer");
				return root.nextMove_pos;
			}
			if (rootChildSize > 10 && current_time < 10.0)
			{
				return root.nextMove_pos;
			}
			
			/*
			//*****check***
			if (cur_node == root)
			{
				//System.out.println("@Root: next_pos: "+cur_node.nextMove_pos.row +","+cur_node.nextMove_pos.col +":" +cur_node.nextMove_pos.val);
		
			//****************
			 */
			
			// 4. Point the top Node in myStack
			cur_node = mystack.peek();
			
			// 5. Check top Node's myChildren
			if (cur_node.mychildren.isEmpty() || cur_node.depth >= DepthLimit)
			{// No children anymore
				if (cur_node.v == false)
				{// (1) terminal node
					cur_node.value = cur_node.myScore;
					
					// update returnValue
					returnValue = cur_node;
					mystack.pop(); 
					continue;
					
				}
				else
				{// (2) cur Node's children are all visited
					if (cur_node.depth%2 == 0)
					{// (2-1) Max Node
						if (cur_node.value < returnValue.value)
						{ // need to update Nextmove_pos
							cur_node.value = returnValue.value; //keep terminal score
							cur_node.nextMove_pos = returnValue.pos;
							cur_node.nextMove_pos.val = cur_node.value; 
						}
						
						// update returnValue
						returnValue = cur_node;
						
						// (+) alpha-beta pruning
						if (returnValue.value >= cur_node.beta)
						{// cut off
							//System.out.println("CUT OFF");
							mystack.pop(); 
							continue;
						}
						cur_node.alpha = Math.max(cur_node.alpha, returnValue.value);
					}
					else
					{// (2-2) Min Node
						if (cur_node.value > returnValue.value)
						{ // need to update Nextmove_pos
							cur_node.value = returnValue.value;
							cur_node.nextMove_pos = returnValue.pos;
							cur_node.nextMove_pos.val = cur_node.value; 
						}
						// update returnValue
						returnValue = cur_node;
						
						// (+) alpha-beta pruning
						if (returnValue.value <= cur_node.alpha)
						{// cut off
							//System.out.println("CUT OFF");
							mystack.pop(); 
							continue;
						}
						cur_node.beta = Math.min(cur_node.beta, returnValue.value);
						
					}
				}//else
				
				// (3) cur_node pop! b/c No children
				mystack.pop(); 
				continue;
			}
			else
			{// cur_node has myChildren
				// (1) MiniMax
				if (cur_node.v)
				{
					if (cur_node.depth%2 == 0)
					{// (1-1) Max Node
						if (cur_node.value < returnValue.value)
						{ // need to update Nextmove_pos
							cur_node.value = returnValue.value;
							cur_node.nextMove_pos = returnValue.pos;
							cur_node.nextMove_pos.val = cur_node.value;
						}
						// update returnValue
						returnValue = cur_node;
						
						// (+) alpha-beta pruning
						if (returnValue.value >= cur_node.beta)
						{// cut off
							//System.out.println("CUT OFF!");
							mystack.pop(); 
							continue;
						}
						cur_node.alpha = Math.max(cur_node.alpha, returnValue.value);
						
					}
					else
					{// (1-2) Min Node
						if (cur_node.value > returnValue.value)
						{ // need to update Nextmove_pos
							cur_node.value = returnValue.value;
							cur_node.nextMove_pos = returnValue.pos;
							cur_node.nextMove_pos.val = cur_node.value; 
						}
						// update returnValue
						returnValue = cur_node;
						
						// (+) alpha-beta pruning
						if (returnValue.value <= cur_node.alpha)
						{// cut off
							//System.out.println("CUT OFF");
							mystack.pop(); 
							continue;
						}
						cur_node.beta = Math.min(cur_node.beta, returnValue.value);
					}
				}
				
				// (3) create new child node
				cur_node.v = true; 
				nextChild_pos = cur_node.mychildren.pop();
				Node child = new Node(nextChild_pos);
				child.depth = cur_node.depth+1;
				child.myBoard = new char[N][N];
				for (int i = 0; i < N; i++)
				{
					for (int j = 0; j < N; j++)
					{
						child.myBoard[i][j] = cur_node.myBoard[i][j];
					}
				}
				
				// (2) Initiate visited array
				for (int i = 0; i < N; i++)
				{
					for (int j = 0; j < N; j++)
					{
						if (child.myBoard[i][j] == '*')
						{
							visited[i][j] = true;
						}
						else 
						{
							visited[i][j] = false;
						}
					}
				}
				
				// (4) check child Node's neighbors
				myque.add(nextChild_pos);
				visited[nextChild_pos.row][nextChild_pos.col] = true;
				while(!myque.isEmpty())
				{
					score++;
					poped_pos = myque.poll();
					poped_row = poped_pos.row;
					poped_col = poped_pos.col;
					poped_value = cur_node.myBoard[poped_row][poped_col];
					child.myBoard[poped_row][poped_col] = '*';
					
					// Down
					if ((poped_row < N-1)
							&& poped_value == child.myBoard[poped_row+1][poped_col] 
							&& !visited[poped_row+1][poped_col])
					{
						//System.out.println("down");
						myque.add(new Position(poped_row+1, poped_col));
						visited[poped_row+1][poped_col] = true;
					}
							
					// Up
					if ((poped_row > 0)
							&& poped_value == child.myBoard[poped_row-1][poped_col] 
							&& !visited[poped_row-1][poped_col])
					{
						//System.out.println("up");
						myque.add(new Position(poped_row-1, poped_col));
						visited[poped_row-1][poped_col] = true;
					}
					
															
					// Left
					if ((poped_col > 0)
							&& poped_value == child.myBoard[poped_row][poped_col-1] 
									&& !visited[poped_row][poped_col-1])
					{
						//System.out.println("left");
						myque.add(new Position(poped_row, poped_col-1));
						visited[poped_row][poped_col-1] = true;
					}
			
					// Right
					if ((poped_col < N-1)
							&& (poped_value == child.myBoard[poped_row][poped_col+1]  
									&& !visited[poped_row][poped_col+1]))
					{
						//System.out.println("right");
						myque.add(new Position(poped_row, poped_col+1));
						visited[poped_row][poped_col+1] = true;
					}
					
				}// while: myque
				
				/*
				//*****check*********
				if (cur_node == root)
				{
					System.out.println("I'm root's child: " + child.pos.row + "," +child.pos.col);
					for (int i = 0; i < N; i++)
					{
						for (int j = 0; j < N; j++)
						{
							System.out.print(child.myBoard[i][j]);
						}
						System.out.println();
					}
					System.out.println();
				}
				//******************
				 */
				
				// (5) update child's board
				child.myBoard = updateBoard_star(child.myBoard, N);
				child.alpha = cur_node.alpha;
				child.beta = cur_node.beta;
						
				// (6) update child's score
				if (child.depth%2 == 0)
				{// your turn: Max node
					child.value = Integer.MIN_VALUE;
					child.myScore = cur_node.myScore - (int)(Math.pow(score, 2)); 
					//System.out.println("child score: " + child.myScore);
				}
				else
				{// my turn: Min node
					child.value = Integer.MAX_VALUE;
					child.myScore = cur_node.myScore + (int)(Math.pow(score, 2));
					//System.out.println("child score: " + child.myScore);
				}
				
				score = 0;
				
				// (7) add child's mychildren
				child.mychildren = makeChildrenStack(child, N);
				
				// (8) add child to mystack
				mystack.push(child);
				
			}//else: has children
			
		}// while: mystack
		
		return root.nextMove_pos;
	}

	public static void main(String[] args) {
		
		// 0. start Time Limit
        StartTime = System.currentTimeMillis();

		int N = 0, P = 0, row = 0;
		char[][] board;
		Position nextpos = new Position();
		
		//read input.txt
		try { 
			String fileName = "input.txt"; 
            String line = ""; 
            BufferedReader br = new BufferedReader(new FileReader(fileName)); 
               
            // 1. get board size: (int) N
            line = br.readLine();
            N = Integer.parseInt(line);
            // 2. get number of fruits types: (int) P
            line = br.readLine();
            P = Integer.parseInt(line);
            // 3. time limit: (float) Time_Limit
            line = br.readLine();
            Time_Limit = Float.parseFloat(line);               
            // 4. N x N board: (char[][])
            row = 0;
            board = new char [N][N];
            while(row < N) 
            {
            	line = br.readLine();
            	board[row]=line.toCharArray();
            	row ++;
            } 
            br.close(); 
            
            // *. Set DepthLimit
            if (Time_Limit < Urgent)
            {
            	DepthLimit = 1;
            	Emergency = true;
            }
            
    		// 5. Decide next movement
    		nextpos = decideNextMove(N, P, board);	
    		    
    		// 6. Write to output.txt
	    	fileName = "output.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            
            // 7. print next movement
            String s = String.valueOf((char)(nextpos.col + 65));
            s += Integer.toString((nextpos.row+1));
            System.out.println("My Decision: " + s );
            bw.write(s);
            bw.newLine(); // Enter
            
            // 8. update & print next board
            Position poped_pos;
            int poped_row, poped_col, poped_value;
            boolean visited[][] = new boolean[N][N];
            Queue<Position> myque = new LinkedList<Position>();
            myque.add(nextpos);
			while(!myque.isEmpty())
			{
				poped_pos = myque.poll();
				poped_row = poped_pos.row;
				poped_col = poped_pos.col;
				poped_value = board[poped_row][poped_col];
				board[poped_row][poped_col] = '*';
				visited[poped_row][poped_col] = true;
				
				// Down
				if ((poped_row < N-1)
						&& poped_value == board[poped_row+1][poped_col] 
						&& !visited[poped_row+1][poped_col])
				{
					//System.out.println("down");
					myque.add(new Position(poped_row+1, poped_col));
					visited[poped_row+1][poped_col] = true;
				}
				
				// Up
				if ((poped_row > 0)
						&& poped_value == board[poped_row-1][poped_col] 
						&& !visited[poped_row-1][poped_col])
				{
					//System.out.println("up");
					myque.add(new Position(poped_row-1, poped_col));
					visited[poped_row-1][poped_col] = true;
				}
		
				// Left
				if ((poped_col > 0)
						&& poped_value == board[poped_row][poped_col-1] 
								&& !visited[poped_row][poped_col-1])
				{
					//System.out.println("left");
					myque.add(new Position(poped_row, poped_col-1));
					visited[poped_row][poped_col-1] = true;
				}
		
				// Right
				if ((poped_col < N-1)
						&& (poped_value == board[poped_row][poped_col+1]  
								&& !visited[poped_row][poped_col+1]))
				{
					//System.out.println("right");
					myque.add(new Position(poped_row, poped_col+1));
					visited[poped_row][poped_col+1] = true;
				}
				
			}// while: myque
			 
			board = updateBoard_star(board, N);
			
			for (int i = 0; i < N; i++)
			{
				for (int j = 0; j < N; j++)
				{
					bw.write(board[i][j]);
				}
				bw.newLine(); // Enter
			}
			
            bw.close();
            
            //9. Print time
            System.out.println("Safe excute time: " + Time_Limit);
            System.out.println("Execute Time: " + ( System.currentTimeMillis() - StartTime) / 1000F );
            
         } catch (IOException e){ 
               e.printStackTrace(); 
         } 
		   
	}

}
