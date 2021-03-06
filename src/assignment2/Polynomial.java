package assignment2;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author Kamy moussavi
 * @id 260807441
 * TODO make sure all methods can accept empty polynomials
 */

public class Polynomial 
{
	private SLinkedList<Term> polynomial;
	
	public int size()
	{	
		return polynomial.size();
	}
	
	private Polynomial(SLinkedList<Term> p)
	{
		polynomial = p;
	}
	
	public Polynomial()
	{
		polynomial = new SLinkedList<Term>();
	}
	
	// Returns a deep copy of the object.
	public Polynomial deepClone()
	{	
		return new Polynomial(polynomial.deepClone());
	}

	public void addTerm(Term t)
	{	
		
		int size = polynomial.size();

		// The polynomial is empty -> set as first term
		if (size == 0) {
			polynomial.addFirst(t);
			return;
		}
		
		int i = 0;
		int termExponent = t.getExponent();
		int currentExponent;
		BigInteger coeffSum;

		for (Term currentTerm: polynomial)
		{	
			currentExponent = currentTerm.getExponent();

			// The term's exponent is largest in list -> add first
			if (i == 0 && currentExponent < termExponent) {
				polynomial.addFirst(t);
				return;
			}
			// The current term has same exponent -> add coefficients
			else if (currentExponent == termExponent) {
				coeffSum = currentTerm.getCoefficient().add(t.getCoefficient());
				
				// If coefficients add to 0 -> delete term
				if (coeffSum.intValue() == 0) {
					polynomial.remove(i);
					return;
				}

				currentTerm.setCoefficient(coeffSum);
				return;
			} 
			// The term's exponent is smallest in list
			else if (i + 1 == size && currentExponent > termExponent) {
				polynomial.addLast(t);
				return;
			}
			// The term is added before the current one
			else if (currentExponent < termExponent) {
				polynomial.add(i,t);
				return;
			}
			i++;
		}
	}
	
	public Term getTerm(int index)
	{
		return polynomial.get(index);
	}
	
	public static Polynomial add(Polynomial p1, Polynomial p2)
	{
		// // Empty polynomial safety check
		// Polynomial sum = new Polynomial();
		
		// if (p1.size() == 0 && p2.size() == 0){
		// 	return sum;
		// } else if (p1.size() == 0) {
		// 	return p2.deepClone();
		// } else if (p2.size() == 0){
		// 	return p1.deepClone();
		// }
	
		// Iterator<Term> p1Iter = p1.getLinkedList().iterator();
		// Iterator<Term> p2Iter = p2.getLinkedList().iterator();
		
		// Term p1Term = p1.getTerm(0);
		// Term p2Term = p2.getTerm(0);

		// while (p1Iter.hasNext() || p2Iter.hasNext()) {
		// 	// if same exponent, add them
		// 	// if one of them larger, add first check next
		// 	if((p1Iter.hasNext() && p2Iter.hasNext()) && (p1Term.getExponent() == p2Term.getExponent())){
		// 		sum.addTermLast(new Term(p1Term.getExponent(), p1Term.getCoefficient().add(p2Term.getCoefficient())));
		// 		p1Term = p1Iter.next();
		// 		p2Term = p2Iter.next();
		// 	} else if (p1Term.getExponent() > p2Term.getExponent()) {
		// 		sum.addTermLast(new Term(p1Term.getExponent(), p1Term.getCoefficient()));
		// 		p1Term = p1Iter.next();
		// 	} else if (p1Term.getExponent() < p2Term.getExponent()) {
		// 		sum.addTermLast(new Term(p2Term.getExponent(), p2Term.getCoefficient()));
		// 		p2Term = p1Iter.next();
		// 	}
		// }
		// return sum;

		Polynomial sum = p1.deepClone();
		
		for (Term term: p2.polynomial) {
			sum.addTerm(term);
		}
	
		return sum;

	}

	private void multiplyTerm(Term t)
	{	
		int termExponent = t.getExponent();
		BigInteger termCoefficient = t.getCoefficient();

		if (termCoefficient.intValue() == 0) {
			polynomial = new SLinkedList<Term>();
		}

		for (Term currentTerm: polynomial)
		{	
			currentTerm.setCoefficient(currentTerm.getCoefficient().multiply(termCoefficient));
			currentTerm.setExponent(currentTerm.getExponent() + termExponent);
		}
	}

	public static Polynomial multiply(Polynomial p1, Polynomial p2)
	{
		Polynomial temp = new Polynomial();
		Polynomial sum = new Polynomial();
		
		for (int i = 0; i < p2.size(); i++) {
			// get a copy of p1
			temp = p1.deepClone();
			
			// multiply it by the current p2 term
			temp.multiplyTerm(p2.getTerm(i));

			// add it to the sum
			sum = Polynomial.add(sum,temp);
		}


		return sum;
	}
	
	//TODO: evaluate this polynomial.
	// Hint:  The time complexity of eval() must be order O(m), 
	// where m is the largest degree of the polynomial. Notice 
	// that the function SLinkedList.get(index) method is O(m), 
	// so if your eval() method were to call the get(index) 
	// method m times then your eval method would be O(m^2).
	// Instead, use a Java enhanced for loop to iterate through 
	// the terms of an SLinkedList.

	public BigInteger eval(BigInteger x)
	{
		BigInteger result = new BigInteger("0");
		BigInteger currentCoeff;
		Term lastTerm;
		int currentExponent;
		int lastExponent = Integer.MAX_VALUE;
		
		// if x = 0, check if theres a term without exponents and return coefficient, else 0
		if (x.equals(new BigInteger("0"))){
			lastTerm = polynomial.removeLast();
			if (lastTerm.getExponent() == 0) {
				result = result.add(lastTerm.getCoefficient());
			}
			else return result;
		}
		
		// if poly is size 0 -> return 0
		if(polynomial.size() == 0) {
		 	return result;
		}

		// if poly is size 1 -> simple calculation
		if(polynomial.size() == 1) {
			result = result.add(polynomial.get(0).getCoefficient());
			for (int i = 0; i < polynomial.get(0).getExponent(); i++)
				result = result.multiply(x);
			return result;
		}

		// if poly is complex -> do this shit
		boolean firstRun = true;
		for (Term currentTerm: polynomial){
			currentExponent = currentTerm.getExponent();
			currentCoeff = currentTerm.getCoefficient();
			
			if (!firstRun){ 
				// if the next term doesnt have any coefficients -> multiply by x and continue
				while (currentExponent != lastExponent-1) {
					result = result.multiply(x);
					lastExponent--;
				}

				// if last element after eliminating inbetween elements is 0exp -> add coeff and finish
				if (currentExponent == 0){
				 	result = result.add(currentCoeff);
					break;
				}
				
				result = result.add(currentCoeff).multiply(x);
				lastExponent = currentExponent;
			}

			// If the exponent is 0 -> add last coefficient to result and stop looping
			if (currentExponent == 0){
				result = result.add(currentCoeff);
				break;
			}
			
			// if its the first run -> set initial
			if (firstRun){
				lastExponent = currentExponent;
				result = result.add(currentCoeff).multiply(x);
				firstRun = false;
				continue;
			}
		}

		// if the last exponent in the loop was larger than 1 -> multiply by 
		while (lastExponent > 1) {
			result = result.multiply(x);
			lastExponent--;
		}

		return result;
	}
	
	// Checks if this polynomial is same as the polynomial in the argument.
	// Used for testing whether two polynomials have same content but occupy disjoint space in memory.
	// Do not change this code, doing so may result in incorrect grades.
	public boolean checkEqual(Polynomial p)
	{	
		// Test for null pointer exceptions!!
		// Clearly two polynomials are not same if they have different number of terms
		if (polynomial == null || p.polynomial == null || size() != p.size())
			return false;
		
		int index = 0;
		// Simultaneously traverse both this polynomial and argument. 
		for (Term term0 : polynomial)
		{
			// This is inefficient, ideally you'd use iterator for sequential access.
			Term term1 = p.getTerm(index);
			
			if (term0.getExponent() != term1.getExponent() || // Check if the exponents are not same
				term0.getCoefficient().compareTo(term1.getCoefficient()) != 0 || // Check if the coefficients are not same
				term1 == term0) // Check if the both term occupy same memory location.
					return false;
			
			index++;
		}
		return true;
	}
	
	// This method blindly adds a term to the end of LinkedList polynomial. 
	// Avoid using this method in your implementation as it is only used for testing.
	// Do not change this code, doing so may result in incorrect grades.
	public void addTermLast(Term t)
	{	
		polynomial.addLast(t);
	}
	
	// This is used for testing multiplyTerm.
	// Do not change this code, doing so may result in incorrect grades.
	public void multiplyTermTest(Term t)
	{
		multiplyTerm(t);
	}
	
	@Override
	public String toString()
	{	
		if (polynomial.size() == 0) return "0";
		return polynomial.toString();
	}

	private SLinkedList<Term> getLinkedList(){
		return polynomial;
	}
}
