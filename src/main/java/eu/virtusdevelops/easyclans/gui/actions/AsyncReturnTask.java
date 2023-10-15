package eu.virtusdevelops.easyclans.gui.actions;

public interface AsyncReturnTask<T> {
    T fetchPageData(int page, int perPage);
    T fetchData();
}
