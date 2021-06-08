public class Member {
    private final String name;
    private final String num;
    private int freq;
    private final int phoneNum;

    public Member(String num, String name, int freq, int phoneNum){
        this.num=num;
        this.name=name;
        this.freq=freq;
        this.phoneNum=phoneNum;
    }

    public String getName() {
        return name;
    }

    public int getFreq() {
        return freq;
    }

    public String getNum() {
        return num;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setFreq(){
        this.freq+=1;
    }
}
