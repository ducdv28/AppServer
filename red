for(int num=10;num<=10;num++){
			String fileInput= "Test/inputs/GT0"+num+".txt";
//			String fileInput ="Test/inputs/input0.txt";
			File fileInputt = new File(fileInput);
			if(fileInputt.exists()){
			FileInputStream file = new FileInputStream(fileInputt);
			BufferedReader input = new BufferedReader(new InputStreamReader(file));
		
			String path="Test/SUM/GT"+num+"_sum.txt";	
//			String path="Test/output.txt";
			File fileOutput = new File(path);
			if (fileOutput.exists()) {
				fileOutput.delete();
			}
			
			try {
				fileOutput.getParentFile().mkdirs(); 
				fileOutput.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		FileOutputStream file1 = new FileOutputStream(fileOutput);
		PrintWriter output= new PrintWriter(file1);
		//String line;
