package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {

	public static String getDataDiferencaDias(Integer qtdDias) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, qtdDias);
		//formatar em string
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(cal);
	}
}
