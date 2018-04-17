package com.paraverity.weighttrack

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

class WeightEntry
private constructor(var date: Int, var weight: Float, var c1: Boolean, var c2: Boolean, notes: String) : Comparable<WeightEntry> {
    var notes: String
    protected var dm: DisplayMode

    init {
        this.notes = notes ?: ""
        dm = toDisplayMode()
    }

    override fun compareTo(entry: WeightEntry): Int {
        return entry.date - this.date
    }

    class DisplayMode internal constructor(var date: String, var weight: String, var c1: String, var c2: String, var notes: String) {
        override fun toString(): String {
            return "$date, $weight, $c1, $c2, $notes"
        }
    }

    fun toDisplayMode(): WeightEntry.DisplayMode {
        val d = "" + this.date
        val str = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6)
        return DisplayMode(
                str,
                this.weight.toString() + "kg",
                if (this.c1) "✔" else "-",
                if (this.c2) "✔" else "-",
                this.notes
        )
    }

    override fun equals(o: Any?): Boolean {
        if (o !is WeightEntry) return false
        val we = o
        return date == we.date && weight == we.weight && c1 == we.c1 && c2 == we.c2 && notes == we.notes
    }

    companion object {

        fun create(date: Int, weight: Float, c1: Boolean, c2: Boolean, notes: String): WeightEntry {
            return WeightEntry(date, weight, c1, c2, notes)
        }
    }
}
