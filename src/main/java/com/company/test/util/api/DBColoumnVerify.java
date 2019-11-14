package com.company.test.util.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import com.company.test.exception.DatabaseException;
import com.company.test.util.common.DatabaseOperation;

public class DBColoumnVerify extends DatabaseOperation {
	protected String CondColName;

	public DBColoumnVerify() {

	}

	public DBColoumnVerify(String CondColName) {
		this.CondColName = CondColName;
	}

	public boolean DbCol(LinkedHashMap<String, String> row) throws DatabaseException {
		return ConditionReading(row.get(CondColName), row);
	}

	public boolean ConditionReading(String condition, LinkedHashMap<String, String> row) throws DatabaseException {
		boolean ConditionReading = false;

		if (condition.equals("")) {
			ConditionReading = true;
			return ConditionReading;
		} else {
			String[] splits = condition.split(";");
			int length = splits.length;

			for (int i = 0; i < length; i++) {
				if (splits[i].contains("||")) {
					List<Boolean> oRBoolArray = new ArrayList<Boolean>();
					String[] ORsplits = splits[i].split("\\|\\|");
					for (int j = 0; j < ORsplits.length; j++) {
						oRBoolArray.addAll(processOR(ORsplits[j], row));
					}

					if (oRBoolArray.contains(true)) {
						ConditionReading = true;
					} else {
						ConditionReading = false;
						return ConditionReading;
					}

				} else {
					ConditionReading = false;
					String[] CondValue = new String[20];
					String operator = null;

					if (splits[i].contains(">=")) {
						CondValue = splits[i].split(">=");
						operator = ">=";
					} else if (splits[i].contains("<=")) {
						CondValue = splits[i].split("<=");
						operator = "<=";
					} else if (splits[i].contains("=")) {
						CondValue = splits[i].split("=");
						operator = "=";
					} else if (splits[i].contains("<>")) {
						CondValue = splits[i].split("<>");
						operator = "<>";
					} else if (splits[i].contains(">")) {
						CondValue = splits[i].split(">");
						operator = ">";
					} else if (splits[i].contains("<")) {
						CondValue = splits[i].split("<");
						operator = "<";
					} else if (splits[i].contains("!{}")) {
						CondValue = splits[i].split("!\\{\\}");
						String cond = CondValue[0];
						if (!(row.get(cond).trim().isEmpty())) {
							ConditionReading = true;
							return ConditionReading;
						} else {
							ConditionReading = false;
							return ConditionReading;

						}
					} else if (splits[i].contains("{}")) {
						CondValue = splits[i].split("\\{\\}");
						String cond = CondValue[0];
						if (row.get(cond).trim().isEmpty()) {
							ConditionReading = true;
							return ConditionReading;
						} else {
							ConditionReading = false;
							return ConditionReading;
						}
					}

					String cond = CondValue[0];
					String value = CondValue[1];
					//System.out.println(value + "-----" + cond + "-----" + operator + "------");
					String[] individualValue = value.split("\\|");
					try {
						if (cond.equals("EffectiveDate") || cond.equals("ExpirationDate")) {
							for (int j = 0; j < individualValue.length; j++) {
								switch (operator) {
								case "=":
									if (isEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
										ConditionReading = true;
									}
									break;
								case "<>":
									if (!(isEqual(row.get(cond), individualValue[j], "yyyy-MM-dd"))) {
										ConditionReading = false;
										return ConditionReading;
									} else {
										ConditionReading = true;
									}
									break;
								case ">":
									if (isBefore(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case "<":
									if (isAfter(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case ">=":
									if (isBeforeOrEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case "<=":
									if (isAfterOrEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								}
							}
						} else {
							for (int j = 0; j < individualValue.length; j++) {
								switch (operator) {
								case "=":
									if ((row.get(cond).equals(individualValue[j]))) {
										ConditionReading = true;
									}
									break;
								case "<>":
									if ((row.get(cond).equals(individualValue[j]))) {
										ConditionReading = false;
										return ConditionReading;
									} else {
										ConditionReading = true;
									}
									break;
								case ">":
									if (Integer.parseInt(row.get(cond)) > Integer.parseInt(individualValue[j])) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case "<":
									if (Integer.parseInt(row.get(cond)) < Integer.parseInt(individualValue[j])) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case ">=":
									if (Integer.parseInt(row.get(cond)) >= Integer.parseInt(individualValue[j])) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								case "<=":
									if (Integer.parseInt(row.get(cond)) <= Integer.parseInt(individualValue[j])) {
										ConditionReading = true;
										return ConditionReading;
									} else {
										ConditionReading = false;
									}
									break;
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (!ConditionReading) {
						return ConditionReading;
					}
				}
			}
		}

		return ConditionReading;
	}

	public List<Boolean> processOR(String conditionss, LinkedHashMap<String, String> row) {
		List<Boolean> oRBoolArray = new ArrayList<Boolean>();
		boolean ConditionReading = false;

		ConditionReading = false;
		String[] CondValue = new String[20];
		String operator = null;

		if (conditionss.contains(">=")) {
			CondValue = conditionss.split(">=");
			operator = ">=";
		} else if (conditionss.contains("<=")) {
			CondValue = conditionss.split("<=");
			operator = "<=";
		} else if (conditionss.contains("=")) {
			CondValue = conditionss.split("=");
			operator = "=";
		} else if (conditionss.contains("<>")) {
			CondValue = conditionss.split("<>");
			operator = "<>";
		} else if (conditionss.contains(">")) {
			CondValue = conditionss.split(">");
			operator = ">";
		} else if (conditionss.contains("<")) {
			CondValue = conditionss.split("<");
			operator = "<";
		} else if (conditionss.contains("!{}")) {
			CondValue = conditionss.split("!\\{\\}");
			String cond = CondValue[0];
			if (!(row.get(cond).trim().isEmpty())) {
				ConditionReading = true;
				oRBoolArray.add(ConditionReading);
			} else {
				ConditionReading = false;
				oRBoolArray.add(ConditionReading);

			}
		} else if (conditionss.contains("{}")) {
			CondValue = conditionss.split("\\{\\}");
			String cond = CondValue[0];
			if (row.get(cond).trim().isEmpty()) {
				ConditionReading = true;
				oRBoolArray.add(ConditionReading);
			} else {
				ConditionReading = false;
				oRBoolArray.add(ConditionReading);
			}
		}

		String cond = CondValue[0];
		String value = CondValue[1];
		// System.out.println(value+"-----"+cond+operator+"------"+conditionss);
		String[] individualValue = value.split("\\|");
		try {
			if (cond.equals("EffectiveDate") || cond.equals("ExpirationDate")) {
				for (int j = 0; j < individualValue.length; j++) {
					switch (operator) {
					case "=":
						if (isEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<>":
						if (!(isEqual(row.get(cond), individualValue[j], "yyyy-MM-dd"))) {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case ">":
						if (isBefore(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<":
						if (isAfter(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case ">=":
						if (isBeforeOrEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<=":
						if (isAfterOrEqual(row.get(cond), individualValue[j], "yyyy-MM-dd")) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					}

				}
			} else {

				for (int j = 0; j < individualValue.length; j++) {
					switch (operator) {
					case "=":
						if ((row.get(cond).equals(individualValue[j]))) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<>":
						if ((row.get(cond).equals(individualValue[j]))) {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case ">":
						if (Integer.parseInt(row.get(cond)) > Integer.parseInt(individualValue[j])) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<":
						if (Integer.parseInt(row.get(cond)) < Integer.parseInt(individualValue[j])) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case ">=":
						if (Integer.parseInt(row.get(cond)) >= Integer.parseInt(individualValue[j])) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					case "<=":
						if (Integer.parseInt(row.get(cond)) <= Integer.parseInt(individualValue[j])) {
							ConditionReading = true;
							oRBoolArray.add(ConditionReading);
						} else {
							ConditionReading = false;
							oRBoolArray.add(ConditionReading);
						}
						break;
					}

				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return oRBoolArray;

	}

	/*
	 * public static void main(String args[]) throws DatabaseException {
	 * DatabaseOperation db = new DatabaseOperation();
	 * DatabaseOperation.ConnectionSetup("com.mysql.jdbc.Driver",
	 * "jdbc:mysql://192.168.84.225:3700/CommercialAuto_Development_ADMIN?useSSL=false",
	 * "root", "redhat"); StringBuffer temp1 = new StringBuffer(); DBColoumnVerify
	 * cont = new DBColoumnVerify(); LinkedHashMap<Integer, LinkedHashMap<String,
	 * String>> input
	 * =db.GetDataObjects("Select * from ConditionTable_CA_FormSelection");
	 * 
	 * LinkedHashMap<Integer, LinkedHashMap<String, String>> input1
	 * =db.GetDataObjects("Select * from Input_FormSelection"); for (Entry<Integer,
	 * LinkedHashMap<String, String>> entry2 : input1.entrySet()) {
	 * LinkedHashMap<String, String> inputrow1 = entry2.getValue();
	 * 
	 * for (Entry<Integer, LinkedHashMap<String, String>> entry1 : input.entrySet())
	 * { LinkedHashMap<String, String> inputrow = entry1.getValue();
	 * if(inputrow.get("Flag_for_execution").equals("Y")) { String s =
	 * inputrow.get("Condition");
	 * System.out.println(inputrow.get("S_No")+"-----"+s);
	 * System.out.println(cont.ConditionReading(s,inputrow1));
	 * //System.out.println(getWords(inputrow.get("Condition"))); } } }
	 * DatabaseOperation.CloseConn(); }
	 */

	private boolean isBefore(String Date1, String Date2, String Format) {
		try {
			return stringtoDate(Date2, Format).before(stringtoDate(Date1, Format));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isBeforeOrEqual(String Date1, String Date2, String Format) {
		try {
			return stringtoDate(Date2, Format).before(stringtoDate(Date1, Format))
					|| stringtoDate(Date2, Format).equals(stringtoDate(Date1, Format));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isAfter(String Date1, String Date2, String Format) throws ParseException {
		try {
			return stringtoDate(Date2, Format).after(stringtoDate(Date1, Format));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean isAfterOrEqual(String Date1, String Date2, String Format) throws ParseException {
		try {
			return stringtoDate(Date2, Format).after(stringtoDate(Date1, Format))
					|| stringtoDate(Date2, Format).equals(stringtoDate(Date1, Format));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isEqual(String Date1, String Date2, String Format) throws ParseException {
		try {
			return stringtoDate(Date2, Format).equals(stringtoDate(Date1, Format));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Date stringtoDate(String date, String dateFormat) throws ParseException {
		DateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		return format.parse(date);
	}

	/*
	 * public static void main(String args[]) throws ParseException {
	 * System.out.println(isBefore("2019-01-11","2019-01-10", "yyyy-MM-dd")); }
	 */

}
