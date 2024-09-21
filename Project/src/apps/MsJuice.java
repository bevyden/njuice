package apps;

public class MsJuice {
    
    private String JuiceId;
    private String JuiceName;
    private Integer Price;
    private String JuiceDescription;

    public MsJuice(String JuiceId, String JuiceName, Integer Price, String JuiceDescription) {
        super();
        this.JuiceId = JuiceId;
        this.JuiceName = JuiceName;
        this.Price = Price;
        this.JuiceDescription = JuiceDescription;
    }

    public String getJuiceId() {
        return JuiceId;
    }

    public void setJuiceId(String JuiceId) {
        this.JuiceId = JuiceId;
    }

    public String getJuiceName() {
        return JuiceName;
    }

    public void setJuiceName(String JuiceName) {
        this.JuiceName = JuiceName;
    }

    public Integer getPrice() {
        return Price;
    }

    public void setPrice(Integer Price) {
        this.Price = Price;
    }

    public String getJuiceDesc() {
        return JuiceDescription;
    }

    public void setJuiceDesc(String JuiceDescription) {
        this.JuiceDescription = JuiceDescription;
    }
}
