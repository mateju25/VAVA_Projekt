package project.model.databaseSystem;
//vytvorim na zaciatku programu len jeden ktory bude false a pocas behu programu to budem menit
public class Tournament {
    private byte format;
    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public byte getFormat() {
        return format;
    }
}
