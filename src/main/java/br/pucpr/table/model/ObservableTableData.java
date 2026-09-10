package br.pucpr.table.model;

public interface ObservableTableData extends TableData {
  void addListener(TableDataListener listener);

  void removeListener(TableDataListener listener);
}