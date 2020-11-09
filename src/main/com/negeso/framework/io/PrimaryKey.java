package com.negeso.framework.io;

import java.io.Serializable;

public interface PrimaryKey<PK extends Serializable>{
	PK[] getJoinedPrimaryKey();
	Object[] getJoined();
	void setJoinedObject(Object key, int foreign);
}
