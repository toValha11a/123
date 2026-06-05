package arena;

public class UnitData {
    private String internalName;
    private String displayName;
    private int statPri1;      // Атака первым оружием
    private int statPri2;      // Натиск первым оружием
    private int statPri4;      // Дальность полёта снаряда
    private int statSec1;      // Атака вторым оружием
    private int statSec2;      // Натиск вторым оружием
    private int statPriArmour1; // Уровень доспеха
    private int statPriArmour2; // Защита (умение)
    private int statPriArmour3; // Уровень щита

    public UnitData(String internalName, String displayName) {
        this.internalName = internalName;
        this.displayName = displayName;
    }

    // Геттеры и сеттеры
    public String getInternalName() { return internalName; }
    public void setInternalName(String internalName) { this.internalName = internalName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getStatPri1() { return statPri1; }
    public void setStatPri1(int statPri1) { this.statPri1 = statPri1; }

    public int getStatPri2() { return statPri2; }
    public void setStatPri2(int statPri2) { this.statPri2 = statPri2; }

    public int getStatPri4() { return statPri4; }
    public void setStatPri4(int statPri4) { this.statPri4 = statPri4; }

    public int getStatSec1() { return statSec1; }
    public void setStatSec1(int statSec1) { this.statSec1 = statSec1; }

    public int getStatSec2() { return statSec2; }
    public void setStatSec2(int statSec2) { this.statSec2 = statSec2; }

    public int getStatPriArmour1() { return statPriArmour1; }
    public void setStatPriArmour1(int statPriArmour1) { this.statPriArmour1 = statPriArmour1; }

    public int getStatPriArmour2() { return statPriArmour2; }
    public void setStatPriArmour2(int statPriArmour2) { this.statPriArmour2 = statPriArmour2; }

    public int getStatPriArmour3() { return statPriArmour3; }
    public void setStatPriArmour3(int statPriArmour3) { this.statPriArmour3 = statPriArmour3; }

    public int getTotalAttack() {
        return statPri1 + statPri2 + statSec1 + statSec2;
    }

    public int getTotalDefense() {
        return statPriArmour1 + statPriArmour2 + statPriArmour3;
    }

    public boolean hasRangedWeapon() {
        return statPri4 > 0;
    }
}