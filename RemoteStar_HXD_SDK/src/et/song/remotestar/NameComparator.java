package et.song.remotestar;

import java.util.Comparator;
import java.util.Locale;

public class NameComparator implements Comparator<AdapterPYinItem>{

	public int compare(AdapterPYinItem o1, AdapterPYinItem o2) {
//		if (o1.getName().equals("����Ʒ��(Other brands)")){
//			return 1;
//		}
//		if (o2.getName().equals("����Ʒ��(Other brands)")){
//			return -1;
//		}
		return o1.getName().toUpperCase(Locale.getDefault())
				.compareTo(o2.getName().toUpperCase(Locale.getDefault()));

	}

}
