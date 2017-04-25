package summary;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * A simple utility class for handling string manipulation methods , console
 * display functions and so on.
 */
public class Util {
	// ///Xoa toan bo dau cach/////////
	public static String removeAllSpaces(String s) {
		StringTokenizer st = new StringTokenizer(s, " ", false);
		StringBuffer t = new StringBuffer("");
		while (st.hasMoreElements())
			t.append(st.nextElement());
		return t.toString();
	}

	// //////Thay the 2 hoac nhieu dau cach bang 1 dau cach///////
	public static String removeMultipleSpaces(String s) {
		Pattern p = Pattern.compile("[\\s]+");
		Matcher m = p.matcher(" ");
		m.reset(s);
		String result = m.replaceAll(" ");
		return result;
	}

	// /////In ra cac cau theo rank va lay x cau//////////////
	public static void DIPLAYMAP(Map<?, ?> map) {
		Iterator<?> iter = map.keySet().iterator();
		// int counter = 0;
		while (iter.hasNext()) {
			Object key = iter.next();
			INFO(key + " " + map.get(key));
			// counter++;
		}
	}

	// ///Tổng các tích vô hướng của các giá trị trọng số trong câu thứ j và câu
	// thứ k./////////////
	public static double getDotProduct(Double ar1[], Double ar2[]) {
		double res = 0.0;
		// Neu ar1.length lon hon thi tich vo huong phan du cung bang 0
		// Neu ar1.length be hon thi tich vo huong da tinh du
		for (int i = 0; i < ar1.length; i++) {
			res += (ar1[i] * ar2[i]);
		}
		return res;
	}

	// ////////Lấy căn bậc hai của tổng các ô của các phần tử trong
	// vector////////
	public static double getRootOfSumOfSquares(Double ar1[]) {
		double res = 0.0;
		for (int i = 0; i < ar1.length; i++) {
			res += Math.pow(ar1[i], 2.0);
		}
		return Math.sqrt(res);
	}

	public static void INFO(String msg) {
		System.out.println(msg);
	}
}
