package org.techtown.wishmatching

class ListData(
    private var data1: String? = null,
    private var data2: String? = null){
    private var state_like: Int = 0
    fun getData1(): String? {
        return data1
    }
    fun setData1(name: String) {
        this.data1 = data1
    }
    fun getData2(): String? {
        return data2
    }
    fun setData2(address: String) {
        this.data2 = data2
    }

}
