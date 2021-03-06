package lab7;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();
    Map<Long, AdminUnit> idToAdminUnit = new HashMap<>();
    Map<AdminUnit, Long> adminUnitToParentId = new HashMap<>();
    Map<Long, List<AdminUnit>> parentIdToChildren = new HashMap<>();

    /**
     * Czyta rekordy pliku i dodaje do listy
     *
     * @param filename nazwa pliku
     */
    public void read(String filename) {
        CSVReader reader = null;

        try {
            reader = new CSVReader(filename, ",", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (reader != null && reader.next()) {
            AdminUnit adminUnit = new AdminUnit();

            adminUnit.name = reader.get("name");

            try {
                adminUnit.adminLevel = reader.getInt("admin_level");
            } catch (Exception e) {
                adminUnit.adminLevel = -1;
            }

            try {
                adminUnit.population = reader.getInt("population");
            } catch (Exception e) {
                adminUnit.population = -1;
            }

            try {
                adminUnit.area = reader.getDouble("area");
            } catch (Exception e) {
                adminUnit.area = -1;
            }

            try {
                adminUnit.density = reader.getDouble("density");
            } catch (Exception e) {
                adminUnit.density = -1;
            }

            try {
                adminUnit.bbox.xmin = Math.min(
                        Math.min(
                                reader.getInt("x1"),
                                reader.getInt("x2")),
                        Math.min(
                                reader.getInt("x3"),
                                reader.getInt("x4")));
                adminUnit.bbox.ymin = Math.min(
                        Math.min(
                                reader.getInt("y1"),
                                reader.getInt("y2")),
                        Math.min(
                                reader.getInt("y3"),
                                reader.getInt("y4")));
                adminUnit.bbox.xmax = Math.max(
                        Math.max(
                                reader.getInt("x1"),
                                reader.getInt("x2")),
                        Math.max(
                                reader.getInt("x3"),
                                reader.getInt("x4")));
                adminUnit.bbox.ymax = Math.max(
                        Math.max(
                                reader.getInt("y1"),
                                reader.getInt("y2")),
                        Math.max(
                                reader.getInt("y3"),
                                reader.getInt("y4")));
            } catch (Exception e) {
                // Je??eli chocia?? jedna warto???? b??dzie pusta,
                // to ca??y bounding box b??dzie z??y
                adminUnit.bbox.xmin = -1;
                adminUnit.bbox.ymin = -1;
                adminUnit.bbox.xmax = -1;
                adminUnit.bbox.ymax = -1;
            }

            long parentId = -1;
            try {
                parentId = reader.getLong("parent");
            } catch (Exception e) {
                // nie ma parenta
            }

            try {
                this.idToAdminUnit.put(reader.getLong("id"), adminUnit);
            } catch (Exception e) {
                // nie no nie ma opcji, ??e nie b??dzie ID
            }
            this.adminUnitToParentId.put(adminUnit, parentId);

            if (!this.parentIdToChildren.containsKey(parentId)) {
                this.parentIdToChildren.put(parentId, new ArrayList<>());
            }

            this.parentIdToChildren.get(parentId).add(adminUnit);

            // dodaj do listy
            this.units.add(adminUnit);
        }

        for (AdminUnit unit : this.units) {
            long parentId = this.adminUnitToParentId.get(unit);
            unit.parent = this.idToAdminUnit.getOrDefault(parentId, null);
        }

        for (Map.Entry<Long, AdminUnit> entry : this.idToAdminUnit.entrySet()) {
            entry.getValue().children = this.parentIdToChildren.get(entry.getKey());
        }
    }

    /**
     * Wypisuje zawarto???? korzystaj??c z AdminUnit.toString()
     *
     * @param out
     */
    public void list(PrintStream out) {
        for (AdminUnit unit : this.units) {
            out.print(unit);
        }
    }

    /**
     * Wypisuje co najwy??ej limit element??w pocz??wszy od elementu o indeksie offset
     *
     * @param out    - strumie?? wyjsciowy
     * @param offset - od kt??rego elementu rozpocz???? wypisywanie
     * @param limit  - ile (maksymalnie) element??w wypisa??
     */
    public void list(PrintStream out, int offset, int limit) {
        for (int i = 0; i < limit; i++) {
            out.print(this.units.get(i + offset));
        }
    }

    /**
     * Zwraca now?? list?? zawieraj??c?? te obiekty AdminUnit, kt??rych nazwa pasuje do wzorca
     *
     * @param pattern - wzorzec dla nazwy
     * @param regex   - je??li regex=true, u??yj funkcji String matches(); je??li false u??yj funkcji contains()
     * @return podzbi??r element??w, kt??rych nazwy spe??niaj?? kryterium wyboru
     */
    public AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();
        // przeiteruj po zawarto??ci units
        for (AdminUnit unit : this.units) {
            if (regex) {
                if (unit.name.matches(pattern)) {
                    // je??eli nazwa jednostki pasuje do wzorca dodaj do ret
                    ret.units.add(unit);
                }
            } else {
                if (unit.name.contains(pattern)) {
                    // je??eli nazwa jednostki pasuje do wzorca dodaj do ret
                    ret.units.add(unit);
                }
            }
        }
        return ret;
    }

    private void fixMissingValues() {
        for (AdminUnit unit : this.units) {
            unit.fixMissingValues();
        }
    }
}
