package summary;

/**
 * Term count and inverse document count.
 */
public class TCandISC {
	Integer tc;// freq(i,j) tần số của từ i trong câu j
	Integer isc;// This is ni // số câu chứa từ i

	public TCandISC() {
		this.tc = 0;
		this.isc = 0;
	}

	@Override
	public String toString() {
		return tc + " " + isc;
	}

	public void setTc(Integer tc) {
		this.tc = tc;
	}

	public Integer getTc() {
		return this.tc;
	}

	public void setISC(Integer isc) {
		this.isc = isc;
	}

	public Integer getISC() {
		return this.isc;
	}

}
