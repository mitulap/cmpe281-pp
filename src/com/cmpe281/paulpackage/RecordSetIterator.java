package com.cmpe281.paulpackage;

public interface RecordSetIterator {
    void   reset() ;
    byte[] getNextRecord();
    boolean hasMoreRecords() ;
}
