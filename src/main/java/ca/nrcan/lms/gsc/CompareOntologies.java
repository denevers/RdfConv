package ca.nrcan.lms.gsc;

import java.io.File;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Compare two folders and check each triples if it has an equivalent
 * TODO: one options is to provide a namespace matching in a file (this ns matches this ns)
 * @author Eric Boisvert
 * Laboratoire de Cartographie Numérique et de Photogrammétrie
 * Commission géologique du Canada (c) 2018
 * Ressources naturelles Canada
 */

public class CompareOntologies {
	public static void compare(File folder1, File folder2, Lang l)
	{
		if (folder1.isDirectory() && folder2.isDirectory())
		{
		System.out.println(" = Left");
		Model left = loadModelFromFolder(folder1);
		System.out.println(" = Right");
		Model right = loadModelFromFolder(folder2);
		System.out.println(" = Comparing");
		compare(left,right,l);
		}
		else
			System.out.println("Both path must be folders");
	}
	
	public static Model loadModelFromFolder(File f)
	{
		// create a default model (no inference)
		Model m = ModelFactory.createDefaultModel();
		for(File ds : f.listFiles())
		{
		try {
		RDFDataMgr.read(m, ds.getAbsolutePath());
		System.out.println("Loaded "+ ds.getAbsolutePath());
		}
		catch(Exception e)
		{
			System.out.println("Failed to read " + ds.getAbsolutePath());
		}
		}
		
		return m;
		
		
	}
	
	public static void compare(Model left,Model right,Lang l)
	{
		Model diff = left.difference(right);
		RDFDataMgr.write(System.out, diff, l);
		
	}

}
