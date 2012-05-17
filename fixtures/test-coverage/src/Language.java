public interface Language {
    
    public String titleize(String lower);
    
    public String pluralize(int count, String singular);
    
    public String[] joiners(int length);
    
}
