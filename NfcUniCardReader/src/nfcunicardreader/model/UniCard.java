package nfcunicardreader.model;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UniCard implements Comparable<UniCard> {
	private String marticulationNumber;
	private Date birthDate;
	private Date validDate;
	private String nfcData;
	private Boolean parsed;
	private String notes;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMarticulationNumber() {
		return marticulationNumber;
	}

	public void setMarticulationNumber(String marticulationNumber) {
		this.marticulationNumber = marticulationNumber;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public String getNfcData() {
		return nfcData;
	}

	public void setNfcData(String nfcData) {
		this.nfcData = nfcData;
	}

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat formatterDateOutput = new SimpleDateFormat(
			"yyyy-MM-dd");
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat formatterDateInput = new SimpleDateFormat(
			"ddMMyyyy");

	public UniCard(String read_data) {
		nfcData = read_data;
		marticulationNumber = read_data.substring(3, 10);
		try {
			birthDate = formatterDateInput.parse(read_data.substring(16, 24));
			validDate = formatterDateInput.parse(read_data.substring(32, 42));
		} catch (ParseException e) {
			e.printStackTrace();
			parsed = false;
		}
		parsed = true;
		notes = "";
	}

	public String toStringNewLines() {
		return toString("\n");
	}

	@Override
	public String toString() {
		return toString(" ");
	}

	public String toString(String delimiter) {
		if (parsed) {
			return String.format("m#: %s%sbirthday: %s%svalid: %s",
					marticulationNumber, delimiter,
					formatterDateOutput.format(birthDate), delimiter,
					formatterDateOutput.format(validDate));
		} else {
			return nfcData;
		}
	}

	public String toCsv() {
		if (parsed) {
			return String.format("%s;%s;%s;%s", marticulationNumber,
					formatterDateOutput.format(birthDate),
					formatterDateOutput.format(validDate),
					notes);
		} else {
			return nfcData;
		}

	}

	@Override
	public int compareTo(UniCard another) {
		return getMarticulationNumber().compareTo(
				another.getMarticulationNumber());
	}
}
