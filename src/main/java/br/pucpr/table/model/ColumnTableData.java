package br.pucpr.table.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ColumnTableData<T> implements ObservableTableData {
  private final List<ColumnData<? super T>> columns;
  private final List<T> data;
  private final List<TableDataListener> listeners;

  public ColumnTableData(
      Collection<? extends T> data, Collection<? extends ColumnData<? super T>> columns) {
    this.columns = new ArrayList<>(columns);
    this.data = new ArrayList<>(data);
    this.listeners = new ArrayList<>();
  }

  @SafeVarargs
  public ColumnTableData(Collection<? extends T> data, ColumnData<? super T>... columns) {
    this(data, Arrays.asList(columns));
  }

  @Override
  public int rowCount() {
    return data.size();
  }

  @Override
  public int colCount() {
    return columns.size();
  }

  @Override
  public String header(int col) {
    return columns.get(col).header();
  }

  @Override
  public String get(int row, int col) {
    var line = data.get(row);
    return columns.get(col).get(line);
  }

  @Override
  public void addListener(TableDataListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    listeners.add(listener);
  }

  @Override
  public void removeListener(TableDataListener listener) {
    listeners.remove(listener);
  }

  public void addRow(T row) {
    data.add(row);
    notifyDataChanged();
  }

  public void addRows(Collection<? extends T> rows) {
    data.addAll(rows);
    notifyDataChanged();
  }

  public T removeRow(int row) {
    final var removed = data.remove(row);
    notifyDataChanged();
    return removed;
  }

  public T setRow(int row, T value) {
    final var previous = data.set(row, value);
    notifyDataChanged();
    return previous;
  }

  public void clearRows() {
    data.clear();
    notifyDataChanged();
  }

  private void notifyDataChanged() {
    for (var listener : listeners) {
      listener.onDataChanged(this);
    }
  }
}
