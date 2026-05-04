package com.example.rubikscube

import kotlin.random.Random

// Face indices
const val FACE_UP = 0
const val FACE_DOWN = 1
const val FACE_FRONT = 2
const val FACE_BACK = 3
const val FACE_LEFT = 4
const val FACE_RIGHT = 5

// Colors per face (solved state)
val FACE_COLORS = intArrayOf(
    0xFFFFFFFF.toInt(), // UP    = white
    0xFFFFFF00.toInt(), // DOWN  = yellow
    0xFF00AA00.toInt(), // FRONT = green
    0xFF0000FF.toInt(), // BACK  = blue
    0xFFFF6600.toInt(), // LEFT  = orange
    0xFFDD0000.toInt()  // RIGHT = red
)

data class CubeState(
    // faces[face][row][col]  row 0 = top, col 0 = left when looking at face
    val faces: Array<Array<IntArray>> = Array(6) { f ->
        Array(3) { IntArray(3) { FACE_COLORS[f] } }
    }
) {
    fun copy(): CubeState {
        val newFaces = Array(6) { f -> Array(3) { r -> faces[f][r].copyOf() } }
        return CubeState(newFaces)
    }

    fun isSolved(): Boolean {
        for (f in 0..5) {
            val color = faces[f][0][0]
            for (r in 0..2) for (c in 0..2) {
                if (faces[f][r][c] != color) return false
            }
        }
        return true
    }

    // Rotate a single face 90° clockwise (in place on a copy)
    private fun rotateFaceCW(face: Int): CubeState {
        val s = copy()
        val f = faces[face]
        s.faces[face][0][0] = f[2][0]; s.faces[face][0][1] = f[1][0]; s.faces[face][0][2] = f[0][0]
        s.faces[face][1][0] = f[2][1]; s.faces[face][1][1] = f[1][1]; s.faces[face][1][2] = f[0][1]
        s.faces[face][2][0] = f[2][2]; s.faces[face][2][1] = f[1][2]; s.faces[face][2][2] = f[0][2]
        return s
    }

    private fun rotateFaceCCW(face: Int): CubeState = rotateFaceCW(face).rotateFaceCW(face).rotateFaceCW(face)

    // ---- public move API ----

    fun moveU(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_UP) else rotateFaceCCW(FACE_UP)
        val tmp = IntArray(3)
        if (cw) {
            // top row: FRONT->RIGHT->BACK->LEFT cycle
            tmp[0] = s.faces[FACE_FRONT][0][0]; tmp[1] = s.faces[FACE_FRONT][0][1]; tmp[2] = s.faces[FACE_FRONT][0][2]
            s.faces[FACE_FRONT][0][0] = s.faces[FACE_LEFT][0][0]; s.faces[FACE_FRONT][0][1] = s.faces[FACE_LEFT][0][1]; s.faces[FACE_FRONT][0][2] = s.faces[FACE_LEFT][0][2]
            s.faces[FACE_LEFT][0][0] = s.faces[FACE_BACK][0][0]; s.faces[FACE_LEFT][0][1] = s.faces[FACE_BACK][0][1]; s.faces[FACE_LEFT][0][2] = s.faces[FACE_BACK][0][2]
            s.faces[FACE_BACK][0][0] = s.faces[FACE_RIGHT][0][0]; s.faces[FACE_BACK][0][1] = s.faces[FACE_RIGHT][0][1]; s.faces[FACE_BACK][0][2] = s.faces[FACE_RIGHT][0][2]
            s.faces[FACE_RIGHT][0][0] = tmp[0]; s.faces[FACE_RIGHT][0][1] = tmp[1]; s.faces[FACE_RIGHT][0][2] = tmp[2]
        } else {
            tmp[0] = s.faces[FACE_FRONT][0][0]; tmp[1] = s.faces[FACE_FRONT][0][1]; tmp[2] = s.faces[FACE_FRONT][0][2]
            s.faces[FACE_FRONT][0][0] = s.faces[FACE_RIGHT][0][0]; s.faces[FACE_FRONT][0][1] = s.faces[FACE_RIGHT][0][1]; s.faces[FACE_FRONT][0][2] = s.faces[FACE_RIGHT][0][2]
            s.faces[FACE_RIGHT][0][0] = s.faces[FACE_BACK][0][0]; s.faces[FACE_RIGHT][0][1] = s.faces[FACE_BACK][0][1]; s.faces[FACE_RIGHT][0][2] = s.faces[FACE_BACK][0][2]
            s.faces[FACE_BACK][0][0] = s.faces[FACE_LEFT][0][0]; s.faces[FACE_BACK][0][1] = s.faces[FACE_LEFT][0][1]; s.faces[FACE_BACK][0][2] = s.faces[FACE_LEFT][0][2]
            s.faces[FACE_LEFT][0][0] = tmp[0]; s.faces[FACE_LEFT][0][1] = tmp[1]; s.faces[FACE_LEFT][0][2] = tmp[2]
        }
        return s
    }

    fun moveD(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_DOWN) else rotateFaceCCW(FACE_DOWN)
        val tmp = IntArray(3)
        if (cw) {
            tmp[0] = s.faces[FACE_FRONT][2][0]; tmp[1] = s.faces[FACE_FRONT][2][1]; tmp[2] = s.faces[FACE_FRONT][2][2]
            s.faces[FACE_FRONT][2][0] = s.faces[FACE_RIGHT][2][0]; s.faces[FACE_FRONT][2][1] = s.faces[FACE_RIGHT][2][1]; s.faces[FACE_FRONT][2][2] = s.faces[FACE_RIGHT][2][2]
            s.faces[FACE_RIGHT][2][0] = s.faces[FACE_BACK][2][0]; s.faces[FACE_RIGHT][2][1] = s.faces[FACE_BACK][2][1]; s.faces[FACE_RIGHT][2][2] = s.faces[FACE_BACK][2][2]
            s.faces[FACE_BACK][2][0] = s.faces[FACE_LEFT][2][0]; s.faces[FACE_BACK][2][1] = s.faces[FACE_LEFT][2][1]; s.faces[FACE_BACK][2][2] = s.faces[FACE_LEFT][2][2]
            s.faces[FACE_LEFT][2][0] = tmp[0]; s.faces[FACE_LEFT][2][1] = tmp[1]; s.faces[FACE_LEFT][2][2] = tmp[2]
        } else {
            tmp[0] = s.faces[FACE_FRONT][2][0]; tmp[1] = s.faces[FACE_FRONT][2][1]; tmp[2] = s.faces[FACE_FRONT][2][2]
            s.faces[FACE_FRONT][2][0] = s.faces[FACE_LEFT][2][0]; s.faces[FACE_FRONT][2][1] = s.faces[FACE_LEFT][2][1]; s.faces[FACE_FRONT][2][2] = s.faces[FACE_LEFT][2][2]
            s.faces[FACE_LEFT][2][0] = s.faces[FACE_BACK][2][0]; s.faces[FACE_LEFT][2][1] = s.faces[FACE_BACK][2][1]; s.faces[FACE_LEFT][2][2] = s.faces[FACE_BACK][2][2]
            s.faces[FACE_BACK][2][0] = s.faces[FACE_RIGHT][2][0]; s.faces[FACE_BACK][2][1] = s.faces[FACE_RIGHT][2][1]; s.faces[FACE_BACK][2][2] = s.faces[FACE_RIGHT][2][2]
            s.faces[FACE_RIGHT][2][0] = tmp[0]; s.faces[FACE_RIGHT][2][1] = tmp[1]; s.faces[FACE_RIGHT][2][2] = tmp[2]
        }
        return s
    }

    fun moveF(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_FRONT) else rotateFaceCCW(FACE_FRONT)
        val tmp = IntArray(3)
        if (cw) {
            // bottom row of UP -> right col of RIGHT -> top row of DOWN (reversed) -> left col of LEFT (reversed)
            tmp[0] = s.faces[FACE_UP][2][0]; tmp[1] = s.faces[FACE_UP][2][1]; tmp[2] = s.faces[FACE_UP][2][2]
            s.faces[FACE_UP][2][0] = s.faces[FACE_LEFT][2][2]; s.faces[FACE_UP][2][1] = s.faces[FACE_LEFT][1][2]; s.faces[FACE_UP][2][2] = s.faces[FACE_LEFT][0][2]
            s.faces[FACE_LEFT][0][2] = s.faces[FACE_DOWN][0][0]; s.faces[FACE_LEFT][1][2] = s.faces[FACE_DOWN][0][1]; s.faces[FACE_LEFT][2][2] = s.faces[FACE_DOWN][0][2]
            s.faces[FACE_DOWN][0][0] = s.faces[FACE_RIGHT][2][0]; s.faces[FACE_DOWN][0][1] = s.faces[FACE_RIGHT][1][0]; s.faces[FACE_DOWN][0][2] = s.faces[FACE_RIGHT][0][0]
            s.faces[FACE_RIGHT][0][0] = tmp[0]; s.faces[FACE_RIGHT][1][0] = tmp[1]; s.faces[FACE_RIGHT][2][0] = tmp[2]
        } else {
            tmp[0] = s.faces[FACE_UP][2][0]; tmp[1] = s.faces[FACE_UP][2][1]; tmp[2] = s.faces[FACE_UP][2][2]
            s.faces[FACE_UP][2][0] = s.faces[FACE_RIGHT][0][0]; s.faces[FACE_UP][2][1] = s.faces[FACE_RIGHT][1][0]; s.faces[FACE_UP][2][2] = s.faces[FACE_RIGHT][2][0]
            s.faces[FACE_RIGHT][0][0] = s.faces[FACE_DOWN][0][2]; s.faces[FACE_RIGHT][1][0] = s.faces[FACE_DOWN][0][1]; s.faces[FACE_RIGHT][2][0] = s.faces[FACE_DOWN][0][0]
            s.faces[FACE_DOWN][0][0] = s.faces[FACE_LEFT][0][2]; s.faces[FACE_DOWN][0][1] = s.faces[FACE_LEFT][1][2]; s.faces[FACE_DOWN][0][2] = s.faces[FACE_LEFT][2][2]
            s.faces[FACE_LEFT][0][2] = tmp[2]; s.faces[FACE_LEFT][1][2] = tmp[1]; s.faces[FACE_LEFT][2][2] = tmp[0]
        }
        return s
    }

    fun moveB(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_BACK) else rotateFaceCCW(FACE_BACK)
        val tmp = IntArray(3)
        if (cw) {
            tmp[0] = s.faces[FACE_UP][0][0]; tmp[1] = s.faces[FACE_UP][0][1]; tmp[2] = s.faces[FACE_UP][0][2]
            s.faces[FACE_UP][0][0] = s.faces[FACE_RIGHT][0][2]; s.faces[FACE_UP][0][1] = s.faces[FACE_RIGHT][1][2]; s.faces[FACE_UP][0][2] = s.faces[FACE_RIGHT][2][2]
            s.faces[FACE_RIGHT][0][2] = s.faces[FACE_DOWN][2][2]; s.faces[FACE_RIGHT][1][2] = s.faces[FACE_DOWN][2][1]; s.faces[FACE_RIGHT][2][2] = s.faces[FACE_DOWN][2][0]
            s.faces[FACE_DOWN][2][0] = s.faces[FACE_LEFT][0][0]; s.faces[FACE_DOWN][2][1] = s.faces[FACE_LEFT][1][0]; s.faces[FACE_DOWN][2][2] = s.faces[FACE_LEFT][2][0]
            s.faces[FACE_LEFT][0][0] = tmp[2]; s.faces[FACE_LEFT][1][0] = tmp[1]; s.faces[FACE_LEFT][2][0] = tmp[0]
        } else {
            tmp[0] = s.faces[FACE_UP][0][0]; tmp[1] = s.faces[FACE_UP][0][1]; tmp[2] = s.faces[FACE_UP][0][2]
            s.faces[FACE_UP][0][0] = s.faces[FACE_LEFT][2][0]; s.faces[FACE_UP][0][1] = s.faces[FACE_LEFT][1][0]; s.faces[FACE_UP][0][2] = s.faces[FACE_LEFT][0][0]
            s.faces[FACE_LEFT][0][0] = s.faces[FACE_DOWN][2][2]; s.faces[FACE_LEFT][1][0] = s.faces[FACE_DOWN][2][1]; s.faces[FACE_LEFT][2][0] = s.faces[FACE_DOWN][2][0]
            s.faces[FACE_DOWN][2][0] = s.faces[FACE_RIGHT][2][2]; s.faces[FACE_DOWN][2][1] = s.faces[FACE_RIGHT][1][2]; s.faces[FACE_DOWN][2][2] = s.faces[FACE_RIGHT][0][2]
            s.faces[FACE_RIGHT][0][2] = tmp[0]; s.faces[FACE_RIGHT][1][2] = tmp[1]; s.faces[FACE_RIGHT][2][2] = tmp[2]
        }
        return s
    }

    fun moveL(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_LEFT) else rotateFaceCCW(FACE_LEFT)
        val tmp = IntArray(3)
        if (cw) {
            tmp[0] = s.faces[FACE_UP][0][0]; tmp[1] = s.faces[FACE_UP][1][0]; tmp[2] = s.faces[FACE_UP][2][0]
            s.faces[FACE_UP][0][0] = s.faces[FACE_BACK][2][2]; s.faces[FACE_UP][1][0] = s.faces[FACE_BACK][1][2]; s.faces[FACE_UP][2][0] = s.faces[FACE_BACK][0][2]
            s.faces[FACE_BACK][0][2] = s.faces[FACE_DOWN][2][0]; s.faces[FACE_BACK][1][2] = s.faces[FACE_DOWN][1][0]; s.faces[FACE_BACK][2][2] = s.faces[FACE_DOWN][0][0]
            s.faces[FACE_DOWN][0][0] = s.faces[FACE_FRONT][0][0]; s.faces[FACE_DOWN][1][0] = s.faces[FACE_FRONT][1][0]; s.faces[FACE_DOWN][2][0] = s.faces[FACE_FRONT][2][0]
            s.faces[FACE_FRONT][0][0] = tmp[0]; s.faces[FACE_FRONT][1][0] = tmp[1]; s.faces[FACE_FRONT][2][0] = tmp[2]
        } else {
            tmp[0] = s.faces[FACE_UP][0][0]; tmp[1] = s.faces[FACE_UP][1][0]; tmp[2] = s.faces[FACE_UP][2][0]
            s.faces[FACE_UP][0][0] = s.faces[FACE_FRONT][0][0]; s.faces[FACE_UP][1][0] = s.faces[FACE_FRONT][1][0]; s.faces[FACE_UP][2][0] = s.faces[FACE_FRONT][2][0]
            s.faces[FACE_FRONT][0][0] = s.faces[FACE_DOWN][0][0]; s.faces[FACE_FRONT][1][0] = s.faces[FACE_DOWN][1][0]; s.faces[FACE_FRONT][2][0] = s.faces[FACE_DOWN][2][0]
            s.faces[FACE_DOWN][0][0] = s.faces[FACE_BACK][2][2]; s.faces[FACE_DOWN][1][0] = s.faces[FACE_BACK][1][2]; s.faces[FACE_DOWN][2][0] = s.faces[FACE_BACK][0][2]
            s.faces[FACE_BACK][0][2] = tmp[2]; s.faces[FACE_BACK][1][2] = tmp[1]; s.faces[FACE_BACK][2][2] = tmp[0]
        }
        return s
    }

    fun moveR(cw: Boolean = true): CubeState {
        var s = if (cw) rotateFaceCW(FACE_RIGHT) else rotateFaceCCW(FACE_RIGHT)
        val tmp = IntArray(3)
        if (cw) {
            tmp[0] = s.faces[FACE_UP][0][2]; tmp[1] = s.faces[FACE_UP][1][2]; tmp[2] = s.faces[FACE_UP][2][2]
            s.faces[FACE_UP][0][2] = s.faces[FACE_FRONT][0][2]; s.faces[FACE_UP][1][2] = s.faces[FACE_FRONT][1][2]; s.faces[FACE_UP][2][2] = s.faces[FACE_FRONT][2][2]
            s.faces[FACE_FRONT][0][2] = s.faces[FACE_DOWN][0][2]; s.faces[FACE_FRONT][1][2] = s.faces[FACE_DOWN][1][2]; s.faces[FACE_FRONT][2][2] = s.faces[FACE_DOWN][2][2]
            s.faces[FACE_DOWN][0][2] = s.faces[FACE_BACK][2][0]; s.faces[FACE_DOWN][1][2] = s.faces[FACE_BACK][1][0]; s.faces[FACE_DOWN][2][2] = s.faces[FACE_BACK][0][0]
            s.faces[FACE_BACK][0][0] = tmp[2]; s.faces[FACE_BACK][1][0] = tmp[1]; s.faces[FACE_BACK][2][0] = tmp[0]
        } else {
            tmp[0] = s.faces[FACE_UP][0][2]; tmp[1] = s.faces[FACE_UP][1][2]; tmp[2] = s.faces[FACE_UP][2][2]
            s.faces[FACE_UP][0][2] = s.faces[FACE_BACK][2][0]; s.faces[FACE_UP][1][2] = s.faces[FACE_BACK][1][0]; s.faces[FACE_UP][2][2] = s.faces[FACE_BACK][0][0]
            s.faces[FACE_BACK][0][0] = s.faces[FACE_DOWN][2][2]; s.faces[FACE_BACK][1][0] = s.faces[FACE_DOWN][1][2]; s.faces[FACE_BACK][2][0] = s.faces[FACE_DOWN][0][2]
            s.faces[FACE_DOWN][0][2] = s.faces[FACE_FRONT][0][2]; s.faces[FACE_DOWN][1][2] = s.faces[FACE_FRONT][1][2]; s.faces[FACE_DOWN][2][2] = s.faces[FACE_FRONT][2][2]
            s.faces[FACE_FRONT][0][2] = tmp[0]; s.faces[FACE_FRONT][1][2] = tmp[1]; s.faces[FACE_FRONT][2][2] = tmp[2]
        }
        return s
    }

    fun applyMove(move: Int): CubeState = when (move) {
        0 -> moveU(true); 1 -> moveU(false)
        2 -> moveD(true); 3 -> moveD(false)
        4 -> moveF(true); 5 -> moveF(false)
        6 -> moveB(true); 7 -> moveB(false)
        8 -> moveL(true); 9 -> moveL(false)
        10 -> moveR(true); 11 -> moveR(false)
        else -> this
    }

    fun shuffle(moves: Int = 20): Pair<CubeState, List<Int>> {
        var state = this
        val moveList = mutableListOf<Int>()
        var lastMove = -1
        repeat(moves) {
            var m: Int
            do { m = Random.nextInt(12) } while (m / 2 == lastMove / 2)
            state = state.applyMove(m)
            moveList.add(m)
            lastMove = m
        }
        return Pair(state, moveList)
    }

    override fun equals(other: Any?) = other is CubeState && faces.contentDeepEquals(other.faces)
    override fun hashCode() = faces.contentDeepHashCode()
}

val MOVE_NAMES = arrayOf("U", "U'", "D", "D'", "F", "F'", "B", "B'", "L", "L'", "R", "R'")
