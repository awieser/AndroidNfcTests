package nfclogger.db.model;

public class CategorySummary extends CategoryBase {
	
	private Category category;

	public CategorySummary(Category c) {
		this.category = c;
	}

	@Override
	public String toString() {
		
		long seconds = category.getTimeSum();
		
		long minutes = seconds / 60;
		seconds %= 60;
		
		long hours = minutes / 60;
		minutes %= 60;
		
		long days = hours / 24;
		hours %= 24;		 
		 
		return category.getName() + " " + 
			days + "d " + 
			hours + "h " + 
			minutes + "min " + 
			seconds + "sec ";
	}

}
