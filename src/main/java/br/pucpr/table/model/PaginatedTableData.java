package br.pucpr.table.model;

import java.util.ArrayList;
import java.util.List;

public class PaginatedTableData implements ObservableTableData, TableDataListener {
  private final TableData source;
  private final List<TableDataListener> listeners;
  private int pageSize;
  private int page;

  public PaginatedTableData(TableData source, int pageSize) {
    if (source == null) {
      throw new IllegalArgumentException("Source cannot be null");
    }
    if (pageSize <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero");
    }

    this.source = source;
    this.pageSize = pageSize;
    this.page = 0;
    this.listeners = new ArrayList<>();

    if (source instanceof ObservableTableData observable) {
      observable.addListener(this);
    }
  }

  public int page() {
    return page;
  }

  public int pageSize() {
    return pageSize;
  }

  public int pageCount() {
    final var rows = source.rowCount();
    if (rows == 0) {
      return 1;
    }
    return (rows + pageSize - 1) / pageSize;
  }

  public void setPage(int page) {
    if (page < 0 || page >= pageCount()) {
      throw new IndexOutOfBoundsException("Page out of bounds: " + page);
    }
    if (this.page != page) {
      this.page = page;
      notifyDataChanged();
    }
  }

  public boolean nextPage() {
    if (page + 1 >= pageCount()) {
      return false;
    }
    page++;
    notifyDataChanged();
    return true;
  }

  public boolean previousPage() {
    if (page == 0) {
      return false;
    }
    page--;
    notifyDataChanged();
    return true;
  }

  public void setPageSize(int pageSize) {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero");
    }
    this.pageSize = pageSize;
    clampPage();
    notifyDataChanged();
  }

  @Override
  public int rowCount() {
    final var start = rowStart();
    final var rows = source.rowCount() - start;
    return Math.max(0, Math.min(pageSize, rows));
  }

  @Override
  public int colCount() {
    return source.colCount();
  }

  @Override
  public String header(int col) {
    return source.header(col);
  }

  @Override
  public String get(int row, int col) {
    if (row < 0 || row >= rowCount()) {
      throw new IndexOutOfBoundsException("Row out of bounds: " + row);
    }
    return source.get(rowStart() + row, col);
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

  @Override
  public void onDataChanged(TableData source) {
    if (source == this.source) {
      clampPage();
      notifyDataChanged();
    }
  }

  private int rowStart() {
    return page * pageSize;
  }

  private void clampPage() {
    final var max = pageCount() - 1;
    if (page > max) {
      page = Math.max(0, max);
    }
  }

  private void notifyDataChanged() {
    for (var listener : listeners) {
      listener.onDataChanged(this);
    }
  }
}