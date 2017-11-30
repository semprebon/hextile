package org.semprebon.hextile

import java.lang.Math.*

typealias Point = Pair<Double, Double>
typealias Edge = Pair<Point, Point>

/**
 * Hextile represents a hex tile, currently, a single hex
 *
 *
 * Labelling Points and Edges
 *             0 1             0
 * Vertices:  5 * 2   Edges: 5   1
 *             4 3           4   2
 *                             3
 */
class HexTile<T>(val edges: List<T>) {
    init {
        if (edges.size != 6) throw IllegalArgumentException("Must specify 6 edge values")
    }

    constructor(state: T) : this(EdgeRange.map { _ -> state })

    fun toSVG() {
        """<svg version="1.1" xmlns="http://www.w3.org/2000/svg">
        <polygon points={ HexTile.Vertexes.map((v) => v.toString()).reduce(_ + " " + _) }></polygon>
        </svg>"""
    }

    fun polygons() : List<Polygon<T>> {
        val edgeIndexesAndTypeGroupedByState: Map<T, List<IndexedValue<T>>>
                = edges.withIndex().groupBy { p: IndexedValue<T> -> p.value }

        val edgeIndexesGroupedByState
                = edgeIndexesAndTypeGroupedByState.map { p: IndexedValue<T> -> Pair(p._1, p._2.map { p: Pair<Int, Rit.second }) }

        val polygons: List<Polygon<T>> = edgeIndexesGroupedByState.toSeq
            .flatMap { stateAndEdges ->
                val (state, edges) = stateAndEdges
                val edgePolygons = edges.map { edge: T -> HexTile.polygonForEdge(edge, state) }
                Polygon.merge(edgePolygons)
            }
        return polygons
    }

    companion object {

        val EdgeRange = (0..5)

        val N = 0
        val NE = 1
        val SE = 2
        val S = 3
        val SW = 4
        val NW = 5

        val Vertexes: List<Point> = HexTile.EdgeRange.toList()
            .map { i -> (i + 4) * PI / 3.0 }
            .map { a: Double -> Point(cos(a), -sin(a)) }

        val UnitHexEdgeCoordinates = EdgeRange.map { i -> listOf(Vertexes(i), Vertexes(i % 6)) }

        val UnitHexHeight = Math.sqrt(3.0) / 2.0

        val InternalVertexes = List(-1, 1).map { x -> Point(x*UnitHexHeight/4, 0.0) }

        // This contains the polygon associated with each edge
        val EdgePolygons = listOf<List<Point>>(
            UnitHexEdgeCoordinates[N]  + listOf(InternalVertexes(1), InternalVertexes(0)),
            UnitHexEdgeCoordinates[NE] + listOf(InternalVertexes(1)),
            UnitHexEdgeCoordinates[SE] + listOf(InternalVertexes(1)),
            UnitHexEdgeCoordinates[S]  + listOf(InternalVertexes(0), InternalVertexes(1)),
            UnitHexEdgeCoordinates[SW] + listOf(InternalVertexes(0)),
            UnitHexEdgeCoordinates[NW] + listOf(InternalVertexes(0)))

        fun <T> polygonForEdge(edgeIndex: Int, edgeState: T): Polygon<T> {
            Polygon<T>(edgeState, *EdgePolygons[edgeIndex])
        }
    }
}