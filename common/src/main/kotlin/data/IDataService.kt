package data

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
interface IDataService<E> {

    /**
     * Requests all data
     *
     * @return List of data elements
     */
    fun requestAllData(): List<E>

    /**
     * Requests a page of data elements
     *
     * @param startIndex Index of data element to start
     * @param pageSize Number of elements to request from startIndex
     * @return List of data elements
     */
    fun requestDataPage(startIndex: Int, pageSize: Int): List<E>

}