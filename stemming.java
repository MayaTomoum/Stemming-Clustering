package assingment2_341;
import java.io.*; 
import java.util.*; 


public class stemming {
	static ArrayList<String> stopArray = new ArrayList<String>();

	static List<HashMap<String, Double>> listOfMapsK = new ArrayList<HashMap<String, Double>>();
	static List<HashMap<String, Double>> listOfMapsT = new ArrayList<HashMap<String, Double>>();
	static List<HashMap<String, Double>> AllMaps = new ArrayList<HashMap<String, Double>>();
	static HashMap<String,Double> myMap = new HashMap<>();
	static int [] numOfWords= new int [28]; // first 14 are T then next 14 are K files

	static double [][] matrix = new double [28][28];
	static ArrayList<String> clusters = new ArrayList<String>();

	public static void main(String[] args) throws IOException{
		processing();
		df();
		idf();
		tf();
		tfidf();
		cosinesimilarity();
		printMatrix();
		applyalgorithm();

		//hashmap for idf 
		//array hashmap for tf 

	}

	public static void processing() throws IOException {

		try {
			stopWordRead();	
			for(int j=1; j<15; j++) {
				int count = 0;
				File myFilesT = new File("t" + j + ".txt");
				Scanner myReaderT = new Scanner(myFilesT);
				PrintWriter writerT = new PrintWriter("StemmedT" + j + ".txt"); 
				//Stemmer.stem(myFilesT);
				while (myReaderT.hasNext()) {
					String dataT = myReaderT.next().toLowerCase();
					dataT = removePunctuations(dataT);
					if (stopArray.contains(dataT) == false)
						writerT.println(dataT);
					count++;
				}
				numOfWords[j-1] = count;
				myReaderT.close();
				writerT.close();
			}
			for(int j=1; j<15; j++) {
				int count = 0;
				File myFilesK = new File("k" + j + ".txt");
				Scanner myReaderK = new Scanner(myFilesK);
				PrintWriter writerK = new PrintWriter("StemmedK" + j + ".txt"); 

				while (myReaderK.hasNext()) {
					String dataT = myReaderK.next().toLowerCase();
					dataT = removePunctuations(dataT);
					if (stopArray.contains(dataT) == false)
						writerK.println(dataT);
					count++;
				}
				numOfWords[j+13] = count;
				myReaderK.close();
				writerK.close();

			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void stopWordRead() {
		try {
			File stopword = new File("stopwords.txt");
			Scanner myStopWords = new Scanner(stopword);

			while (myStopWords.hasNext()) {
				String word = myStopWords.next();
				stopArray.add(word);
			}
			myStopWords.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public static String removePunctuations(String s) {
		String noPunc = "";
		for (Character c : s.toCharArray()) {
			if(Character.isLetter(c))
				noPunc += c;
		}
		return noPunc;
	}

	public static void df() {
		try {
			File file;
			Scanner reader;
			for (int y=0; y<2; y++) {
				for(int i=1; i<=14; i++) {
					if ( y == 0 ) 
						file = new File("StemmedT" + i + ".txt");
					else
						file = new File("StemmedK" + i + ".txt");
					reader = new Scanner(file);
					while (reader.hasNext()) {
						String data = reader.next();
						if (myMap.containsKey(data)) {
							double x = myMap.get(data);
							x++;
							myMap.replace(data, x);
						} else {
							myMap.put(data, (double) 1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//for loop to read all the clean files
		//read a single file
		//get the word
		//if the word is in the hashmap, increment its counter
		//else put it in the hashmap
		//xyz.put(String, new Integer(1))
		//xyz.get(String)
		// + 1
		//xyz.set(String, new Integer(newval)
		//make a counter for tf
	}

	public static void tf() {
		try {
			File file;
			Scanner reader;
			for(int i=1; i<=14; i++) {
				listOfMapsK.add(new HashMap<>());
				listOfMapsT.add(new HashMap<>());

			}

			for (int y=0; y<2; y++) {
				for(int i=1; i<=14; i++) {
					if ( y == 0 ) 
						file = new File("StemmedT" + i + ".txt");
					else
						file = new File("StemmedK" + i + ".txt");
					reader = new Scanner(file);
					while (reader.hasNext()) {
						String data = reader.next();
						if(y == 0) {
							HashMap <String, Double> helper = listOfMapsT.get(i-1);
							if (helper.containsKey(data)) {
								double x = helper.get(data);
								x++;
								helper.replace(data, x);
							} else {
								helper.put(data, 1.0);
							}
						}else {
							HashMap <String, Double> helper = listOfMapsK.get(i-1);
							if (helper.containsKey(data)) {
								double x = helper.get(data);
								x++;
								helper.replace(data, x);
							} else {
								helper.put(data, 1.0);
							}
						}
					}
				}

			}
			for (int l=0; l<2; l++) {
				for(int i=1; i<=14; i++) {
					HashMap <String, Double> helper = null;
					if ( l == 0 ) {
						helper = listOfMapsT.get(i-1);

						//String file = 
						for (String s : helper.keySet() ) {
							double x = helper.get(s);
							x = x / numOfWords[i-1];
							helper.replace(s, x);
						}
					}else {
						helper = listOfMapsK.get(i-1);

						//String file = 
						for (String s : helper.keySet() ) {
							double x = helper.get(s);
							x = x / numOfWords[i+13];
							helper.replace(s, x);
						}
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void idf() {
		for (String s : myMap.keySet()) {
			Double x = myMap.get(s);
			x = Math.log(28/x);
			myMap.replace(s, x);
		}

	}

	private static void tfidf() {
		for (int y=0; y<2; y++) {
			for(int i=1; i<=14; i++) {
				HashMap <String, Double> helper;
				if(y == 0)
					helper = listOfMapsT.get(i-1);
				else
					helper = listOfMapsK.get(i-1);
				for(String s : helper.keySet()) {
					double x = helper.get(s);
					x = x * myMap.get(s);
					helper.replace(s, x);
				}
			}
		}
		//System.out.println("hi");

	}

	private static void cosinesimilarity() {

		for(HashMap hm : listOfMapsK)
			AllMaps.add(hm);
		for(HashMap hm : listOfMapsT)
			AllMaps.add(hm);

		for(int i = 0; i<28; i++) {
			double x = getmagnitude(i); //get the magnitude of a file
			for(int j=0; j<28; j++) {
				double y = getmagnitude(j); //get the magnitude of a file
				double z = dotproduct(i,j);
				matrix[i][j] = z/x/y;
			}
		}

	}


	private static Double dotproduct(int i, int j) {
		// TODO Auto-generated method stub
		//check the tfidf of every word in a file and multiply it by the tfidf value of the the same word in the other file
		double product =0;
		for(String s : AllMaps.get(i).keySet()) {
			Double x = AllMaps.get(i).get(s);
			Double y = AllMaps.get(j).get(s);
			if (y == null)
				y = 0.0;
			product = product + x * y;
		}
		return product;
	}



	private static double getmagnitude(int i) {
		// TODO Auto-generated method stub
		double total = 0;
		for (String s : AllMaps.get(i).keySet()) {
			total = total + Math.pow(AllMaps.get(i).get(s), 2);
		}
		total = Math.sqrt(total);
		return total;
	}


	private static void printMatrix() {
		try {
			PrintWriter writer = new PrintWriter("Matrix.txt");
			for (int j = 0; j < 28; j++) {
				writer.println(Arrays.toString(matrix[j]));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}


	private static void applyalgorithm() {

		//find the max number in the matrix
		//this number means that the 2 files are very similar
		//2 things to watch out for
		//a file and itself are gonna be 1
		//its symmetric across the diagonal (2 exact copies of the values on either side)
		//study only 1 side (lower side)
		double maxVal = 0;
		int x = 0,y=0;
		String file1 = null, file2 =null;
		for(int i =1; i<28; i++){
			for (int j =0; j<i; j++) {
				if (matrix[i][j] > maxVal) {
					maxVal = matrix[i][j];
					x = i;
					y = j;
				}

				if (x <= 14 ) {
					file1 = "T" + x;
				} else {
					file1 = "K" + (x-14);
				}

				if (y > 14 ) {
					file2 = "T" + x;
				} else {
					file2 = "K" + (x-14);
				}

				if (!clusters.contains(file2)) {
					clusters.add(file2);
				} else if (!clusters.contains(file1)) {
					clusters.add(file1);
				}

			
				matrix[x][y] = 0.0;
			}
		
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream ("Clusters.txt", true));
				String newS = file1 + " & " + file2 + " are similar.";
				File clusterFile = new File("Clusters.txt");
				//Scanner reader = new Scanner(clusterFile);
				
					//String line = reader.nextLine();
				//	if (!line.equalsIgnoreCase(newS)) {
						writer.println(newS);
			
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		
		
	}
}
