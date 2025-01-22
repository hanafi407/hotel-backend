package com.hanafi.hanafihotel.coba.myObj;

public class House {
    private String foundation;
    private String roof;
    private String structure;

    private House(HouseBuilder houseBuilder){
        this.foundation= houseBuilder.foundation;
        this.roof= houseBuilder.roof;
        this.structure=houseBuilder.structure;
    }

    public String getFoundation() {
        return foundation;
    }

    public String getRoof() {
        return roof;
    }

    public String getStructure() {
        return structure;
    }

    public static HouseBuilder builder(){
        return new HouseBuilder();
    }

    public static class HouseBuilder{
        private String foundation;
        private String roof;
        private String structure;
        //setter

        public HouseBuilder setFoundation(String foundation) {
            this.foundation = foundation;
            return this;
        }

        public HouseBuilder setRoof(String roof) {
            this.roof = roof;
            return this;
        }

        public HouseBuilder setStructure(String structure) {
            this.structure = structure;
            return this;
        }

        public House build(){
            return new House(this);
        }

    }

    //getter


}
