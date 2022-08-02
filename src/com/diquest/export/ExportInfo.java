package com.diquest.export;

import com.diquest.util.xml.PatentDataMaps;

public interface ExportInfo {

	void setData(PatentDataMaps data);

	void flush();

	void close();
}
