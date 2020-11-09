package com.negeso.module.search;

import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class SimplifiedAnalyzer extends Analyzer {
	  /**
	   * List of typical german stopwords.
	   */
	private String[] GERMAN_STOP_WORDS = { "einer", "eine", "eines", "einem",
			"einen", "der", "die", "das", "dass", "daß", "du", "er", "sie",
			"es", "was", "wer", "wie", "wir", "und", "oder", "ohne", "mit",
			"am", "im", "in", "aus", "auf", "ist", "sein", "war", "wird",
			"ihr", "ihre", "ihres", "als", "für", "von", "mit", "dich", "dir",
			"mich", "mir", "mein", "sein", "kein", "durch", "wegen", "wird" };

	/**
	 * List of typical dutch stopwords.
	 */
	private String[] DUTCH_STOP_WORDS = { "de", "en", "van", "ik", "te", "dat",
			"die", "in", "een", "hij", "het", "niet", "zijn", "is", "was",
			"op", "aan", "met", "als", "voor", "had", "er", "maar", "om",
			"hem", "dan", "zou", "of", "wat", "mijn", "men", "dit", "zo",
			"door", "over", "ze", "zich", "bij", "ook", "tot", "je", "mij",
			"uit", "der", "daar", "haar", "naar", "heb", "hoe", "heeft",
			"hebben", "deze", "u", "want", "nog", "zal", "me", "zij", "nu",
			"ge", "geen", "omdat", "iets", "worden", "toch", "al", "waren",
			"veel", "meer", "doen", "toen", "moet", "ben", "zonder", "kan",
			"hun", "dus", "alles", "onder", "ja", "eens", "hier", "wie",
			"werd", "altijd", "doch", "wordt", "wezen", "kunnen", "ons",
			"zelf", "tegen", "na", "reeds", "wil", "kon", "niets", "uw",
			"iemand", "geweest", "andere" };

	  /**
	   * Contains the stopwords used with the StopFilter.
	   */
	  private Set stopSet = new HashSet();

	  /**
	   * Contains words that should be indexed but not stemmed.
	   */
	  private Set exclusionSet = new HashSet();

	  /**
	   * Builds an analyzer.
	   */
	  public SimplifiedAnalyzer() {
	    stopSet = StopFilter.makeStopSet(GERMAN_STOP_WORDS);
	  }

	  /**
	   * Builds an analyzer with the given stop words.
	   */
	  public SimplifiedAnalyzer(String[] stopwords) {
	    stopSet = StopFilter.makeStopSet(stopwords);
	  }

	  /**
	   * Builds an analyzer with the given stop words.
	   */
	  public SimplifiedAnalyzer(Hashtable stopwords) {
	    stopSet = new HashSet(stopwords.keySet());
	  }

	  /**
	   * Builds an exclusionlist from an array of Strings.
	   */
	  public void setStemExclusionTable(String[] exclusionlist) {
	    exclusionSet = StopFilter.makeStopSet(exclusionlist);
	  }

	  /**
	   * Builds an exclusionlist from a Hashtable.
	   */
	  public void setStemExclusionTable(Hashtable exclusionlist) {
	    exclusionSet = new HashSet(exclusionlist.keySet());
	  }

	  /**
	 * Creates a TokenStream which tokenizes all the text in the provided
	 * Reader.
	   *
	   * @return A TokenStream build from a StandardTokenizer filtered with
	   *         StandardFilter, LowerCaseFilter, StopFilter, GermanStemFilter
	   */
	  public TokenStream tokenStream(String fieldName, Reader reader) {
	    TokenStream result = new StandardTokenizer(reader);
	    result = new StandardFilter(result);
	    result = new LowerCaseFilter(result);
	    result = new StopFilter(result, stopSet);
	    result = new SimplifiedStemFilter(result, exclusionSet);
	    return result;
	  }
}
