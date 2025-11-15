import org.apache.commons.csv.*;
import java.io.*;
import java.util.*;

public class CSVMerger {
    // TODO: add validation here 
        // String test = "output.csv";
        // if (outputFile.equals(test)) {
        //     System.out.println("default output");
        // }
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java CSVMerger <output.csv> <input1.csv> <input2.csv> ...");
            return;
        }
        
        String outputFile = args[0];
        
        List<String> inputFiles = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            inputFiles.add(args[i]);
        }
        
        
        
        try {
            mergeCSVFiles(inputFiles, outputFile);
            System.out.println("Successfully merged " + inputFiles.size() + " files");
            System.out.println("Output saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // private static void Merge(List<String> files) {
    //     StringBuilder sb = new StringBuilder();
    //     // append here
    //     sb.append("header1,header2\n");
    // }
  
    public static void mergeCSVFiles(List<String> inputFiles, String outputFile) throws IOException {
        
        // read the header from the first file
        System.out.println("\nReading first file to get headers");
        String[] headers = getHeadersFromFile(inputFiles.get(0));
        System.out.println("Found " + headers.length + " columns: " + Arrays.toString(headers));
        
        // testing code 
        // FileWriter testWriter = new FileWriter("test.csv");
        // testWriter.write("test,data\n");
        
        // create the output file with headers
        FileWriter fileWriter = new FileWriter(outputFile);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(headers));
        
        int totalRowsWritten = 0;
        
        // process each input file
        for (int i = 0; i < inputFiles.size(); i++) {
            String inputFile = inputFiles.get(i);
            System.out.println("\nProcessing file " + (i + 1) + ": " + inputFile);
            
            // read CSV file
            FileReader fileReader = new FileReader(inputFile);
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            
            // get headers from this file
            Map<String, Integer> fileHeaders = csvParser.getHeaderMap();
            System.out.println("Columns in this file: " + fileHeaders.keySet());
            
            // check if headers match
            if (!headersMatch(headers, fileHeaders.keySet())) {
                csvParser.close();
                csvPrinter.close();
                throw new IOException("Header mismatch in file: " + inputFile + 
                                    "\nExpected: " + Arrays.toString(headers) + 
                                    "\nFound: " + fileHeaders.keySet());
            }
            
            // read and write each row
            int rowCount = 0;
            for (CSVRecord record : csvParser) {
                // create new row in correct column order
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    row.add(record.get(header));
                }
                csvPrinter.printRecord(row);
                rowCount++;
                totalRowsWritten++;
            }
            
            System.out.println("Copied " + rowCount + " rows");
            csvParser.close();
        }
        
        // close output file
        csvPrinter.flush();
        csvPrinter.close();
        
        System.out.println("\n MERGE COMPLETE");
        System.out.println("Total rows written: " + totalRowsWritten);
    }
    
    //validate file extensions 
    // private static boolean validateFile(String filename) {
    //     File f = new File(filename);
    //     // check if csv
    //     return filename.endsWith(".csv");
    // }
   
    private static String[] getHeadersFromFile(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        
        // Get headers as array
        Map<String, Integer> headerMap = csvParser.getHeaderMap();
        String[] headers = new String[headerMap.size()];
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            headers[entry.getValue()] = entry.getKey();
        }
        
        csvParser.close();
        return headers;
    }
    
    // debug method for when testing 
    // private static void debugPrintFile(String filename) throws IOException {
    //     FileReader reader = new FileReader(filename);
    //     BufferedReader br = new BufferedReader(reader);
    //     String line;
    //     while ((line = br.readLine()) != null) {
    //         System.out.println(line);
    //     }
    // }
  
    private static boolean headersMatch(String[] expected, Set<String> actual) {
        if (expected.length != actual.size()) {
            return false;
        }
        Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));
        return expectedSet.equals(actual);
    }
    
  
}

// ============================   TEST CASE FOR RESOURCE CHECKER GENERATE BY AI ===============================

// import org.apache.commons.csv.*;
// import java.io.*;
// import java.util.*;

// public class CSVMerger {
    
//     public static void main(String[] args) {
//         if (args.length < 2) {
//             System.out.println("Usage: java CSVMerger <output.csv> <input1.csv> <input2.csv> ...");
//             return;
//         }
        
//         String outputFile = args[0];
        
//         List<String> inputFiles = new ArrayList<>();
//         for (int i = 1; i < args.length; i++) {
//             inputFiles.add(args[i]);
//         }
        
//         try {
//             mergeCSVFiles(inputFiles, outputFile);
//             System.out.println("Successfully merged " + inputFiles.size() + " files");
//             System.out.println("Output saved to: " + outputFile);
//         } catch (IOException e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     // LEAK 1: FileWriter never closed if exception occurs
//     public static void leakyWrite(String file) throws IOException {
//         FileWriter fw = new FileWriter(file);
//         fw.write("data");
//         // OOPS: forgot to close!
//     }
    
//     // LEAK 2: Reader leaked on exception path
//     public static void conditionalLeak(String file1, String file2) throws IOException {
//         FileReader reader1 = new FileReader(file1);
//         // If this throws, reader1 is leaked!
//         FileReader reader2 = new FileReader(file2);
//         reader1.close();
//         reader2.close();
//     }
    
//     // LEAK 3: Only closes on one branch
//     public static void branchLeak(String file, boolean flag) throws IOException {
//         FileWriter writer = new FileWriter(file);
//         if (flag) {
//             writer.write("data");
//             writer.close();
//         }
//         // LEAK: if flag is false, writer never closes!
//     }
  
//     public static void mergeCSVFiles(List<String> inputFiles, String outputFile) throws IOException {
        
//         System.out.println("\nReading first file to get headers");
//         String[] headers = getHeadersFromFile(inputFiles.get(0));
//         System.out.println("Found " + headers.length + " columns: " + Arrays.toString(headers));
        
//         // LEAK 4 & 5: These resources can leak on exceptions
//         FileWriter fileWriter = new FileWriter(outputFile);
//         CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(headers));
        
//         int totalRowsWritten = 0;
        
//         for (int i = 0; i < inputFiles.size(); i++) {
//             String inputFile = inputFiles.get(i);
//             System.out.println("\nProcessing file " + (i + 1) + ": " + inputFile);
            
//             // LEAK 6 & 7: These can leak on exceptions
//             FileReader fileReader = new FileReader(inputFile);
//             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            
//             Map<String, Integer> fileHeaders = csvParser.getHeaderMap();
//             System.out.println("Columns in this file: " + fileHeaders.keySet());
            
//             if (!headersMatch(headers, fileHeaders.keySet())) {
//                 csvParser.close();
//                 csvPrinter.close();
//                 throw new IOException("Header mismatch in file: " + inputFile);
//             }
            
//             int rowCount = 0;
//             for (CSVRecord record : csvParser) {
//                 List<String> row = new ArrayList<>();
//                 for (String header : headers) {
//                     row.add(record.get(header));
//                 }
//                 csvPrinter.printRecord(row);
//                 rowCount++;
//                 totalRowsWritten++;
//             }
            
//             System.out.println("Copied " + rowCount + " rows");
//             csvParser.close();
//         }
        
//         csvPrinter.flush();
//         csvPrinter.close();
        
//         System.out.println("\n MERGE COMPLETE");
//         System.out.println("Total rows written: " + totalRowsWritten);
//     }
   
//     private static String[] getHeadersFromFile(String filename) throws IOException {
//         // LEAK 8 & 9: These can leak on exceptions
//         FileReader fileReader = new FileReader(filename);
//         CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        
//         Map<String, Integer> headerMap = csvParser.getHeaderMap();
//         String[] headers = new String[headerMap.size()];
//         for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
//             headers[entry.getValue()] = entry.getKey();
//         }
        
//         csvParser.close();
//         return headers;
//     }
  
//     private static boolean headersMatch(String[] expected, Set<String> actual) {
//         if (expected.length != actual.size()) {
//             return false;
//         }
//         Set<String> expectedSet = new HashSet<>(Arrays.asList(expected));
//         return expectedSet.equals(actual);
//     }
// }