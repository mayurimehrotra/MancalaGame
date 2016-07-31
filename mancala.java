import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class mancala {
	private static boolean DEBUG = false ;
	private static int maximisingPlayer;
	private static int cut_off_depth ;
	private static int no_of_moves;
	private static FileReader fileReader=null;
	private static FileWriter fileWriterState=null;
	private static FileWriter fileWriterLog = null;
	private static int task;
	private static BufferedReader bufferedReader=null;
	private static BufferedWriter bufferedWriterState=null;
	private static BufferedWriter bufferedWriterLog=null;	
	private static PrintWriter printWriterState = null;
	private static PrintWriter printWriterLog = null;
	private static int lineCount = 2;
	
	public static void main(String[] args) {
		
		int i=0;
		String fileName = null;
		
		if(args[0].equals("-i"))
			fileName = args[1];
		
			try {
				fileReader = new FileReader(fileName);
				bufferedReader = new BufferedReader(fileReader);
				
				fileWriterState = new FileWriter("next_state.txt");
				fileWriterLog = new FileWriter("traverse_log.txt");
				
				task=Integer.parseInt(bufferedReader.readLine());
				maximisingPlayer = Integer.parseInt(bufferedReader.readLine());
				
				cut_off_depth = Integer.parseInt(bufferedReader.readLine());							
				cut_off_depth= task==1 ? 1 : cut_off_depth; 
								
				//board A
				String rowA= bufferedReader.readLine();
				String boardA[]= rowA.split(" ");
				no_of_moves=boardA.length;
				
				int A[]=new int[boardA.length+2];												
				
				for(i=0;i<boardA.length;i++)				
					A[i+1]=Integer.parseInt(boardA[i]);
			
				//not used
				A[boardA.length+1]=Integer.MIN_VALUE;
								
				//Board B
				String rowB= bufferedReader.readLine();
				String boardB[]= rowB.split(" ");
				int B[]=new int[boardB.length+2];
				
				for(i=0;i<boardB.length;i++)				
					B[i+1]=Integer.parseInt(boardB[i]);
						
				//not used
				B[0]=Integer.MIN_VALUE;				
				
				//player 2's mancala
				A[0]=Integer.parseInt(bufferedReader.readLine());
				
				//player 1's mancala
				B[boardB.length+1]=Integer.parseInt(bufferedReader.readLine());
				
				Node node=new Node();
				node.setA(A);
				node.setB(B);
				node.setDepth(0);
				node.setNextChance(false);
				node.setminMaxValue(Integer.MIN_VALUE);
				node.setRoot(true);
				node.setNodeName("root");				
					
				Node next_state=null;
				

				if(task==1)			
					printWriterState = new PrintWriter(fileWriterState,true);				
				if(task == 2 || task == 3)
				{
					printWriterState = new PrintWriter(fileWriterState,true);
					printWriterLog = new PrintWriter(fileWriterLog,true);
				}
								
				if(task==1)								
					next_state=mini_max(node,maximisingPlayer);				
				else if(task==2)
				{
					printWriterLog.println("Node,Depth,Value");
					printWriterLog.println("root,0,-Infinity");			
					next_state=mini_max(node,maximisingPlayer);
				}
				else if(task==3)
				{
					printWriterLog.println("Node,Depth,Value,Alpha,Beta");
					printWriterLog.println("root,0,-Infinity,-Infinity,Infinity");				
					next_state=mini_max(node,maximisingPlayer);
				}
								
				int [] pitsA = next_state.getA();
				int [] pitsB = next_state.getB();
				
				for(int j = 1; j <= no_of_moves; j++)				
					printWriterState.write(String.valueOf(pitsA[j])+" ");
				
				printWriterState.print("\n");
				
				for(int j = 1; j <= no_of_moves; j++)
					printWriterState.print(pitsB[j]+" ");
				
				printWriterState.print("\n");
				
				printWriterState.println(next_state.getPitCoinsByPlayer(2, 0));
				printWriterState.println(next_state.getPitCoinsByPlayer(1, no_of_moves+1));
								
				if(DEBUG) System.out.println("Next state: " + next_state);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
	}

	
	private static Node mini_max(Node node,int maximisingPlayer) 
	{	
		
		if(task == 1 || task == 2)
			return minimax(node,0,maximisingPlayer);
		if(task == 3)
			return minimax(node,0,maximisingPlayer,Integer.MIN_VALUE,Integer.MAX_VALUE);
		
		return null;
	}

	private static Node minimax(Node node, int depth, int player,int alpha, int beta) 
	{
		
		int opponent; 
		if(player==1)
			opponent=2;
		else
			opponent=1;
		
		if(player == maximisingPlayer)
		{			
			if(endGame(node))
			{
				int minMaxValue=eval(node);
				node.setminMaxValue(minMaxValue);
				return node;
			}
		
			int bestVal= Integer.MIN_VALUE;
			ArrayList<Node> moves = generate_moves(node,player);
			
			for(int j=1;j<=no_of_moves;j++)
			{					
				//check coins for 0-> invalid move
				int coins_check= moves.get(j).getPitCoinsByPlayer(player, j); 
				if(coins_check == 0) 
					continue;
				
				//update values of a move
				Node new_move = applyMove(moves.get(j),player,j);
				boolean isEndGame = false;
				
				if(node.isNextChance())
					new_move.setDepth(node.getDepth());
				else
					new_move.setDepth(node.getDepth()+1);						
				

				if(new_move.isNextChance())
					new_move.setminMaxValue( (new_move.getminMaxValue() == Integer.MAX_VALUE) ?
								Integer.MIN_VALUE : (new_move.getminMaxValue() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : new_move.getminMaxValue()  );
				
			
				if(endGame(new_move))
				{
					if(new_move.getDepth()!=cut_off_depth)
						traverseLog(new_move, player,alpha,beta);	//change
					int minMaxValue=eval(new_move);
					new_move.setminMaxValue(minMaxValue);
					isEndGame = true;					
				}
				
				if (new_move.getDepth() == cut_off_depth )
				{
					if(new_move.isNextChance())
					{
						traverseLog(new_move, player,alpha,beta);
					}
					else
					{
						int minMaxValue=eval(new_move);
						new_move.setminMaxValue(minMaxValue);							
						traverseLog(new_move, player,alpha,beta);
					}					
				}
				else 
					traverseLog(new_move,player,alpha,beta);
				
				Node move=null;
				
				if(! (isEndGame))
				{
					if(new_move.isNextChance())
						move = minimax(new_move, new_move.getDepth(), player,alpha,beta);	//player is 1
					else
					{
						if(new_move.getDepth()==cut_off_depth)
							move=new_move;							
						else
							move = minimax(new_move, new_move.getDepth(), opponent,alpha,beta);
					}					
				}
				else
					move = new_move;
				
				if( node.getminMaxValue() < move.getminMaxValue() )
				{
					int mini_max_val = move.getminMaxValue();
					node.setminMaxValue(mini_max_val);
					
					boolean isPruned = false;
					if( node.getminMaxValue() >= beta )
					{
						isPruned = true;
						if(node.isNextChance()) 
							traverseLog(node,player,alpha,beta); 
						else 
							traverseLog(node,opponent,alpha,beta);
					}
					
					if(alpha<node.getminMaxValue())				
						alpha=node.getminMaxValue();					
					
					if(!isPruned)
					{
						if(node.isNextChance()) 
							traverseLog(node,player,alpha,beta); 
						else 
							traverseLog(node,opponent,alpha,beta);
					}
					if(node.getDepth()==0)
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
					if(node.getDepth()==1 && node.isNextChance())
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
										
					if(beta <= alpha)
						return node;					
				}
				else
					traverseLog(node,player,alpha,beta);
				
			}		//end for children nodes			
			
		}	//end if(maximisingPlayer==1)
		
		else	//if player == opponent
		{
			if(endGame(node))
			{
				int minMaxValue=eval(node);
				node.setminMaxValue(minMaxValue);
				return node;
			}
		
			int bestVal= Integer.MAX_VALUE;
			ArrayList<Node> moves = generate_moves(node,player);			

			for(int j=1;j<=no_of_moves;j++)
			{					
				//check coins for 0-> invalid move
				int coins_check= moves.get(j).getPitCoinsByPlayer(player, j); 
				if(coins_check == 0) 
					continue;
				
				//update values of a move
				Node new_move = applyMove(moves.get(j),player,j);

				if(node.isNextChance())
					new_move.setDepth(node.getDepth());				
				else
					new_move.setDepth(node.getDepth()+1);				
			
				if(new_move.isNextChance())
					new_move.setminMaxValue( (new_move.getminMaxValue() == Integer.MAX_VALUE) ?
								Integer.MIN_VALUE : (new_move.getminMaxValue() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : new_move.getminMaxValue()  );			
				
				boolean isEndGame = false;
				if(endGame(new_move))
				{
					if(new_move.getDepth()!=cut_off_depth)
						traverseLog(new_move, player,alpha,beta);	//change
					int minMaxValue=eval(new_move);
					new_move.setminMaxValue(minMaxValue);
					isEndGame = true;
				}
			
				
				if (new_move.getDepth() == cut_off_depth )
				{
					if(new_move.isNextChance())
					{
						traverseLog(new_move, player,alpha,beta);
					}
					else
					{
						int minMaxValue=eval(new_move);
						new_move.setminMaxValue(minMaxValue);							
						traverseLog(new_move, player,alpha,beta);
					}		
				}
				else 
					traverseLog(new_move,player,alpha,beta);		
				
				Node move=null;
				if(!(isEndGame))
				{
					if(new_move.isNextChance())
						move = minimax(new_move, new_move.getDepth(), player,alpha,beta);	//player is 1
					else
					{
						if(new_move.getDepth()==cut_off_depth)
							move=new_move;											
						else
							move = minimax(new_move, new_move.getDepth(), opponent,alpha,beta);
					}
				}
				else
					move = new_move;
									
				if( node.getminMaxValue() > move.getminMaxValue() )
				{
					int mini_max_val = move.getminMaxValue();
					node.setminMaxValue(mini_max_val);		
					
					boolean isPruned = false;
					if(node.getminMaxValue() <= alpha)
					{
						isPruned = true;
						if(node.isNextChance()) 
							traverseLog(node,player,alpha,beta); 
						else 
							traverseLog(node,opponent,alpha,beta);
					}
					if(node.getminMaxValue() < beta)				
						beta=node.getminMaxValue();
					
					if(!isPruned)
					{
						if(node.isNextChance()) 
							traverseLog(node,player,alpha,beta); 
						else 
							traverseLog(node,opponent,alpha,beta);
					}
					if(node.getDepth()==0)
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
					if(node.getDepth()==1 && node.isNextChance())
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}					
															
					if(beta <= alpha)
						return node;
											
				}
				else
					traverseLog(node,player,alpha,beta);
							
			}//end for children nodes
		}
		return node;				
	}
	

	private static void traverseLog(Node new_move, int player,int alpha,int beta) 
	{
		if(task != 1)
		{
			String playerChar = null;
			String pit=String.valueOf((new_move.getFromIndex()+1));
			if (new_move.isRoot())
				playerChar = "root";
			else 
				playerChar = new_move.getNodeName()+pit; 
			String value = ((new_move.getminMaxValue() == Integer.MAX_VALUE) ? "Infinity" : (new_move.getminMaxValue() == Integer.MIN_VALUE) ? "-Infinity" : String.valueOf(new_move.getminMaxValue()) );
			
			String value1=String.valueOf(value);
			
			if(task==2)
			{
				printWriterLog.println( playerChar + "," + String.valueOf(new_move.getDepth()) + "," + value1 );
				if(DEBUG) System.out.println( ++lineCount + "---->" +playerChar + "," + String.valueOf(new_move.getDepth()) + "," + value1 );
					
			}
			
			if(task==3)
			{
				String alpha1=(alpha==Integer.MAX_VALUE) ? "Infinity" : (alpha ==Integer.MIN_VALUE) ? "-Infinity" : String.valueOf(alpha);				
				String beta1=(beta==Integer.MAX_VALUE) ? "Infinity" : (beta ==Integer.MIN_VALUE) ? "-Infinity" : String.valueOf(beta);				
				printWriterLog.println( playerChar + "," + String.valueOf(new_move.getDepth()) + "," + value1 + "," +alpha1 + ","+beta1);
				if(DEBUG) System.out.println( playerChar + "," + String.valueOf(new_move.getDepth()) + "," + value1 + "," +alpha1 + ","+beta1); 		
			}		
		}
	}

private static Node minimax(Node node, int depth, int player) {
		
		int opponent; 
		if(player==1)
			opponent=2;
		else
			opponent=1;
		
		if(player == maximisingPlayer)
		{			
			if(endGame(node))
			{
				int minMaxValue=eval(node);
				node.setminMaxValue(minMaxValue);
				traverseLog(node, player, 0, 0);
				return node;
			}
		
			int bestVal= Integer.MIN_VALUE;
			ArrayList<Node> moves = generate_moves(node,player);
			
			for(int j=1;j<=no_of_moves;j++)
			{					
				//check coins for 0-> invalid move
				int coins_check= moves.get(j).getPitCoinsByPlayer(player, j); 
				if(coins_check == 0) 
					continue;
				
				//update values of a move
				Node new_move = applyMove(moves.get(j),player,j);		
				if(node.isNextChance())
					new_move.setDepth(node.getDepth());
				
				else
					new_move.setDepth(node.getDepth()+1);						
				
				
				if(new_move.isNextChance())
					new_move.setminMaxValue( (new_move.getminMaxValue() == Integer.MAX_VALUE) ?
								Integer.MIN_VALUE : (new_move.getminMaxValue() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : new_move.getminMaxValue()  );
				
				boolean isEndGame = false;
				if(endGame(new_move))
				{
					if(new_move.getDepth()!=cut_off_depth)
						traverseLog(new_move, player, 0, 0);	//change
					int minMaxValue=eval(new_move);
					new_move.setminMaxValue(minMaxValue);
					isEndGame = true;
				}
					
				
				
				if(DEBUG) System.out.println("---->after setting depth:" + player + " " +new_move);
				
				if (new_move.getDepth() == cut_off_depth )
				{
					if(new_move.isNextChance())
					{
						traverseLog(new_move, player,0,0);
					}
					else
					{
						int minMaxValue=eval(new_move);
						new_move.setminMaxValue(minMaxValue);							
						traverseLog(new_move, player,0,0);
					}
				}
				else 
					traverseLog(new_move,player,0,0);
				
				Node move=null;
				if(!(isEndGame))
				{
					if(new_move.isNextChance())
						move = minimax(new_move, new_move.getDepth(), player);	//player is 1
					else
					{
						if(new_move.getDepth()==cut_off_depth)				
							move=new_move;										
						else
							move = minimax(new_move, new_move.getDepth(), opponent);
					}					
				}
				else
					move = new_move;
				if( node.getminMaxValue() < move.getminMaxValue() )
				{
					int mini_max_val = move.getminMaxValue();
					node.setminMaxValue(mini_max_val);
					if(node.isNextChance()) 
						traverseLog(node,player,0,0); 
					else 
						traverseLog(node,opponent,0,0);
					if(node.getDepth()==0)
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
					if(node.getDepth()==1 && node.isNextChance())
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
				}
				else
					traverseLog(node,player,0,0);				
				
			}		//end for children nodes			
			
		}	//end if(maximisingPlayer==1)
		
		else	//if player == opponent
		{
			if(endGame(node))
			{
				int minMaxValue=eval(node);
				node.setminMaxValue(minMaxValue);
				traverseLog(node, player, 0, 0);
				return node;
			}
		
			int bestVal= Integer.MAX_VALUE;
			ArrayList<Node> moves = generate_moves(node,player);			

			for(int j=1;j<=no_of_moves;j++)
			{					
				//check coins for 0-> invalid move
				int coins_check= moves.get(j).getPitCoinsByPlayer(player, j); 
				if(coins_check == 0) 
					continue;
				
				//update values of a move
				Node new_move = applyMove(moves.get(j),player,j);						

				if(node.isNextChance())
					new_move.setDepth(node.getDepth());				
				else
					new_move.setDepth(node.getDepth()+1);				
				
				if(new_move.isNextChance())
					new_move.setminMaxValue( (new_move.getminMaxValue() == Integer.MAX_VALUE) ?
								Integer.MIN_VALUE : (new_move.getminMaxValue() == Integer.MIN_VALUE) ? Integer.MAX_VALUE : new_move.getminMaxValue()  );
				
				boolean isEndGame = false;
				if(endGame(new_move))
				{
					if(new_move.getDepth()!=cut_off_depth)
						traverseLog(new_move, player, 0, 0);	//change
					int minMaxValue=eval(new_move);
					new_move.setminMaxValue(minMaxValue);
					isEndGame = true;
				}
				
				if(DEBUG) System.out.println("---->after setting depth:" + player + " " +new_move);
				
				if (new_move.getDepth() == cut_off_depth )
				{
					if(new_move.isNextChance()){
						traverseLog(new_move, player,0,0);
					}
					else
					{
						int minMaxValue=eval(new_move);
						new_move.setminMaxValue(minMaxValue);							
						traverseLog(new_move, player,0,0);
					}
				}
				else 
					traverseLog(new_move,player,0,0);		
				
				Node move=null;
				if(!(isEndGame))
				{
					if(new_move.isNextChance())
						move = minimax(new_move, new_move.getDepth(), player);	//player is 1
					else
					{
						if(new_move.getDepth()==cut_off_depth)
							move=new_move;											
						else
							move = minimax(new_move, new_move.getDepth(), opponent);
					}
				}
				else
					move = new_move;
				if( node.getminMaxValue() > move.getminMaxValue() )
				{
					int mini_max_val = move.getminMaxValue();
					node.setminMaxValue(mini_max_val);
					if(node.isNextChance()) 
						traverseLog(node,player,0,0); 
					else 
						traverseLog(node,opponent,0,0);
					if(node.getDepth()==0)		// set root with immediate next state for next state output
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}
					if(node.getDepth()==1 && node.isNextChance())
					{
						node.setA(move.getA());
						node.setB(move.getB());
					}					
				}
				else
					traverseLog(node,player,0,0);
				
			}//end for children nodes			
		}
				
		return node;
	}

	private static int eval(Node new_move) 
	{
		
		int minMaxValue;	
		if(maximisingPlayer==1)
			minMaxValue =new_move.getPitCoinsByPlayer(1, no_of_moves+1)- new_move.getPitCoinsByPlayer(2, 0);
		else
			minMaxValue = new_move.getPitCoinsByPlayer(2, 0)- new_move.getPitCoinsByPlayer(1, no_of_moves+1);
		
		return minMaxValue;
	}


	private static ArrayList<Node> generate_moves(Node parent, int player) 
	{
		
		ArrayList<Node> moves = new ArrayList<Node>();
		moves.add(0,null);
		for (int i = 1; i <= no_of_moves; i++){
			
			int pitsA[] = new int[parent.getA().length];
			pitsA = parent.getA();
			
			int A[] = new int [pitsA.length];
			for (int j = 0 ; j< pitsA.length; j++){
				A[j] = pitsA[j];
			}
			
			int lenB = parent.getB().length;
			int pitsB[] = new int[lenB];
			pitsB = parent.getB();
			
			int B[] = new int [pitsB.length];
			for (int j = 0 ; j< pitsB.length; j++){
				B[j] = pitsB[j];
			}
			
			int depth=parent.getDepth();
			
			Node node=new Node();
			node.setA(A);
			node.setB(B);
			node.setDepth(depth);
			
			node.setminMaxValue(player ==  maximisingPlayer ? Integer.MAX_VALUE : Integer.MIN_VALUE);
			node.setNextChance(false);
			
			moves.add(i,node);
		}
		return moves;
	}
	
	
	private static Node applyMove(Node node, int player,int start_position) 
	{
		/* fill each state while also checking conditions for extra chance and opp block empty			 * 
		also call min_move on these states with opponent as player
		if all pits become zero for any player, end state for game
		*/
		
		int i=0;
		
		//pick up data from parent, store in temp and then save in node
		if(player==1)
		{
			node.setNodeName("B");
			int k=start_position;
			int coins= node.getPitCoinsByPlayer(1, k);			
			int [] pits=node.getPitsByPlayer(player);
						
			pits[k]=0;			
			int flag=0; 		//increment k; for player 1 (B)
			int pitsFlag=0;		//player 1's board B
			
			while(coins > 0)
			{	
				if(pitsFlag==1)	//player 2's board A
				{
					if(k==1 &&coins!=0)
					{
						pits=node.getPitsByPlayer(1);
						pitsFlag=0;		
						k=0;
					}						
				}
				
				if(k==pits.length-1 && coins!=0)
				{
					pits=node.getPitsByPlayer(2);
					pitsFlag=1;					
					flag=1;
				}
				else if(pitsFlag==0)
				{
					flag=0;
				}
				
				if(flag==0) 
					k++;
				else if(flag==1)
					k--;
				
				pits[k]+=1;
				coins--;
			}	// end while
			
			//if last coin ends on same side (empty pit) and opp side pit not empty then mancala updated
			if(pitsFlag==0)
			{
				if(k!=pits.length-1 && k!=0)
				{
					int lastPitcoins=node.getPitCoinsByPlayer(1, k);
					if(lastPitcoins==1)
					{
						coins=node.getPitCoinsByPlayer(2, k);
						if(coins!=0)				
							node.setPitCoinsByPlayer(2, k, 0);
						
						int oldCoins=node.getPitCoinsByPlayer(1, pits.length-1);
						node.setPitCoinsByPlayer(1, pits.length-1,oldCoins+coins +1);
						node.setPitCoinsByPlayer(1, k, 0);
				
					}				
				}
				//ended in mancala
				else if(k==pits.length-1) 	
					node.setNextChance(true);
			}
				
		} //end if player ==1
		
		
		else if(player==2)
		{
			node.setNodeName("A");
			int k=start_position;
			int coins= node.getPitCoinsByPlayer(2, k);			
			int [] pits=node.getPitsByPlayer(2);	
			pits[k]=0;
			
			int flag=0; 		//increment k; for player 2 (A)
			int pitsFlag=0;		//player 2's board A
			
			while(coins > 0)
			{	
				if(pitsFlag==1 &&coins!=0)	//player 1's board B
				{
					if(k==pits.length-2)
					{
						pits=node.getPitsByPlayer(2);
						pitsFlag=0;	
						k=pits.length-1;
					}					
				}
				
				if(k==0 && coins!=0)
				{
					pits=node.getPitsByPlayer(1);
					pitsFlag=1;				
					flag=1;
				}
				else if(pitsFlag==0)
				{
					flag=0;
				}
				
				if(flag==0) 
					k--;
				else if(flag==1)
					k++;
				
				pits[k]+=1;
				coins--;
			
			} //end while
		
			if(pitsFlag==0)
			{
				if(k!=pits.length-1 && k!=0)
				{
					int lastPitcoins=node.getPitCoinsByPlayer(2, k);
					if(lastPitcoins==1)
					{
						coins=node.getPitCoinsByPlayer(1, k);
						if(coins!=0)
							node.setPitCoinsByPlayer(1, k, 0);
						
						int oldCoins=node.getPitCoinsByPlayer(2, 0); // A's mancala old coins
						node.setPitCoinsByPlayer(2, 0 ,oldCoins+coins +1);	//update A's mancala
						node.setPitCoinsByPlayer(2, k, 0);	
					}				
				}
				else if(k==0)				
					node.setNextChance(true);				
			}					
		} //end if player ==2
		
		node.setFromIndex(start_position);		
		return node;
	}
	
	
	static boolean endGame(Node node)
	{
			int [] pitsA = node.getA();
			int [] pitsB = node.getB();
			boolean areAllPitsAZero = true;
			boolean areAllPitsBZero = true;
			
			for(int x=1;x<=no_of_moves;x++)
			{
				if(pitsA[x]!= 0)
				{
					areAllPitsAZero =false;
					break;
				}				
			}
			
			if(areAllPitsAZero)
			{
				int coinsB=node.getPitCoinsByPlayer(1, no_of_moves+1);
				int tempCoinsB=0;
				for(int x=1;x<=no_of_moves;x++)
				{
					tempCoinsB+=pitsB[x];
					pitsB[x]=0;
				}
				node.setPitCoinsByPlayer(1, no_of_moves + 1, coinsB+tempCoinsB);
				
				return true;
			}
			
			for(int x=1;x<=no_of_moves;x++)
			{
				if(pitsB[x]!= 0)
				{
					areAllPitsBZero =false;
					break;
				}				
			}		
			if(areAllPitsBZero)
			{
				int coinsA=node.getPitCoinsByPlayer(2, 0);
				int tempCoinsA=0;
				for(int x=1;x<=no_of_moves;x++)
				{
					tempCoinsA+=pitsA[x];
					pitsA[x]=0;
				}
				node.setPitCoinsByPlayer(2, 0, coinsA+tempCoinsA);				
				
				return true;
			}
			return false;						
		}	

}//end class
