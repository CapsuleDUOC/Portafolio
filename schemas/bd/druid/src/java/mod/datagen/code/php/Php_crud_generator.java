package mod.datagen.code.php;

public interface Php_crud_generator {

	/** For the TableNode produces PHP code implementing a load() method for the PHP class that 
	 * provides a proxy model object for the TableNode. 
	 * 
	 * @return a StringBuffer containing the PHP code for a load() method.
	 */
	public abstract StringBuffer getLoadMethod();

	public abstract StringBuffer getDeleteMethod();
	
	public abstract StringBuffer getSaveMethod();
	
	public abstract StringBuffer getCountMethod();
	
	public abstract StringBuffer getLoadArrayKeyValueSearchMethod();
	
	public abstract StringBuffer getLoadArrayByFullTextMethods();
	
	public abstract StringBuffer getLoadArrayByMethods();
	
	public abstract StringBuffer getKeySelectAllConcatJSONMethod();
	
	public abstract StringBuffer getkeySelectDistinctJSONMethod();
	
	public abstract StringBuffer getSelectDistinctMethods();

	StringBuffer getkeySelectDistinctJSONFilteredMethod();
	
	//public abstract StringBuffer getLoadRelatedMethods();
	
}