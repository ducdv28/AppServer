package summary;

/**
 * Term frequency in a sentence and inverse sentence frequency.
 */
public class TFandISF {
	Double tf;// This is freq(i,j)/maxL freq(L,j)
	Double isf;// This is log (N/ni)

	public TFandISF() {
		this.tf = 0.0;
		this.isf = 0.0;
	}

	@Override
	public String toString() {
		return tf + " " + isf;
	}

	public void setTF(Double tf) {
		this.tf = tf;
	}

	public Double getTF() {
		return this.tf;
	}

	public void setISF(Double isf) {
		this.isf = isf;
	}

	public Double getISF() {
		return this.isf;
	}
}
