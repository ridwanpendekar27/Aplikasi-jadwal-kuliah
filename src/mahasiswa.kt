import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Scanner

data class idJadwal(
    val idJadwal: Int,
    val kelas: String,
    val namaMataKuliah: String,
    val hari: String,
    val jamMulai: String,
    val jamSelesai: String,
    val ruangan: String,
    val dosenPengajar: String
)

fun main() {
    val scanner = Scanner(System.`in`)

    // Database connection details
    val url = "jdbc:mysql://localhost:3306/jadwal_kuliah"
    val user = "root"
    val password = "" // Default password for XAMPP MySQL

    try {
        // Establish connection to the database
        val connection: Connection = DriverManager.getConnection(url, user, password)
        println("🚀 Koneksi ke database berhasil!")

        while (true) {
            println("\n--- Aplikasi Mahasiswa ---")
            println("1. Tampilkan Jadwal")
            println("2. Keluar")
            print("Pilih menu (1-2): ")

            when (scanner.nextLine()) {
                "1" -> {
                    // Menampilkan jadwal dari database
                    val jadwalList = getJadwalFromDatabase(connection)

                    if (jadwalList.isEmpty()) {
                        println("❌ Belum ada jadwal yang ditambahkan.")
                    } else {
                        println("\n-- Daftar Jadwal Perkuliahan --")
                        println("%-5s %-10s %-20s %-10s %-10s %-10s %-10s %-15s".format(
                            "ID", "Kelas", "Nama Mata Kuliah", "Hari", "Mulai", "Selesai", "Ruangan", "Dosen"
                        ))
                        println("-".repeat(100))
                        for (jadwal in jadwalList) {
                            println("%-5s %-10s %-20s %-10s %-10s %-10s %-10s %-15s".format(
                                jadwal.idJadwal, jadwal.kelas, jadwal.namaMataKuliah, jadwal.hari,
                                jadwal.jamMulai, jadwal.jamSelesai, jadwal.ruangan, jadwal.dosenPengajar
                            ))
                        }
                    }
                }

                "2" -> {
                    println("👋 Keluar dari aplikasi. Terima kasih!")
                    break
                }

                else -> println("❗ Pilihan tidak valid, coba lagi!")
            }
        }

    } catch (e: Exception) {
        println("❌ Koneksi ke database gagal: ${e.message}")
    }
}

// Fungsi untuk mengambil jadwal dari database
fun getJadwalFromDatabase(connection: Connection): List<Jadwal> {
    val jadwalList = mutableListOf<Jadwal>()

    try {
        val query = "SELECT * FROM jadwal_perkuliahan"
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)

        while (resultSet.next()) {
            val jadwal = Jadwal(
                idJadwal = resultSet.getInt("idJadwal"),
                kelas = resultSet.getString("kelas"),
                namaMataKuliah = resultSet.getString("namaMataKuliah"),
                hari = resultSet.getString("hari"),
                jamMulai = resultSet.getString("jamMulai"),
                jamSelesai = resultSet.getString("jamSelesai"),
                ruangan = resultSet.getString("ruangan"),
                dosenPengajar = resultSet.getString("dosenPengajar")
            )
            jadwalList.add(jadwal)
        }

    } catch (e: Exception) {
        println("❌ Error fetching data from database: ${e.message}")
    }

    return jadwalList
}