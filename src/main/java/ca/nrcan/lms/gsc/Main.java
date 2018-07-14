package ca.nrcan.lms.gsc;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class Main {

	/**
	 * usage 
	 * -i input file
	 * -f from format (TURTLE RDF/XML N3, JSON-LD etc..)
	 * -t to format
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO, rethink this. original code was for a single action
		Options options = new Options();
		options.addOption("a",true,"action");
		options.addOption("l",true,"Left model");
		options.addOption("r",true,"Right model");
		options.addOption("i",true,"Input file");
		options.addOption("f",true,"Input format");
		options.addOption("t",true,"Output format");
		options.addOption("q",true,"SPARQL Construct query file");
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			String action= cmd.getOptionValue("a");
			// convert or compare, convert is default
			if ("compare".equals(action)) 
			{
				compare(cmd);
				return;
			}
			
			String inputFile = cmd.getOptionValue("i");
			String fromFormat = cmd.getOptionValue("f");  // might be null
			String toFormat = cmd.getOptionValue("t");
			String sparql = cmd.getOptionValue("q");
			

			if (inputFile == null || toFormat == null)
				throw new MissingArgumentException("Missing arguments");
			Model m = inputFile!=null?RDFDataMgr.loadModel(inputFile,getLang(fromFormat)):RDFDataMgr.loadModel(inputFile);
			// write to output stream
			// check if there is a query
			if (sparql != null)
			{
				
				// read from a file
				Query query = QueryFactory.read(sparql) ;
				QueryExecution qexec = QueryExecutionFactory.create(query, m) ;
				Model resultModel = qexec.execConstruct() ;
				qexec.close() ;
				RDFDataMgr.write(System.out, resultModel, getLang(toFormat));
			}
			else
				RDFDataMgr.write(System.out, m, getLang(toFormat));
			
		} catch (ParseException e) {
			
			// TODO Auto-generated catch block
			System.out.println("invalid parameters\n USAGE: -i <file> -f <input format> -t <target format> -q <construct query>");
			System.out.println("Valid formats:" );
			System.out.println("TTL" );
			System.out.println("NT" );
			System.out.println("JSON-LD" );	
			System.out.println("RDF/XML-ABBREV" );
			System.out.println("RDF/XML" );	
			System.out.println("N3" ); 	
			System.out.println("RDF/JSON" );	
		}
		
		
		
		

	}
	
	private static Lang getLang(String s)
	{
		if ("TTL".equalsIgnoreCase(s)) return Lang.TURTLE;
		if ("NT".equalsIgnoreCase(s)) return Lang.NT;
		if ("JSON-LD".equalsIgnoreCase(s)) return Lang.JSONLD;
		if ("RDF-XML-ABBREV".equalsIgnoreCase(s)) return Lang.RDFTHRIFT;
		if ("RDF/XML".equalsIgnoreCase(s)) return Lang.RDFXML;
		if ("N3".equalsIgnoreCase(s)) return Lang.N3;
		if ("RDF/JSON".equalsIgnoreCase(s)) return Lang.RDFJSON;
		return null;
	}
	
	// perform a compare of left and right model
	public static void compare(CommandLine cmd)
	{
		String l = cmd.getOptionValue("l");
		String r = cmd.getOptionValue("r");
		String t = cmd.getOptionValue("t","TTL"); // output format
		if (l == null || r == null )
		{
			System.out.println("Must provide a \"left\" model and a \"right\" model");
			return;
		}
		File fl = new File(l);
		File fr = new File(r);
		CompareOntologies.compare(fl, fr, getLang(t));
		
	}

}
