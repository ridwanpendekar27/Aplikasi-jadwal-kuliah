import java.sql.Connection
import java.sql.DriverManager
import java.util.Scanner

data class Jadwal(
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

    // Create a mutable list to store jadwal
    val jadwalList = mutableListOf<Jadwal>()

    try {
        // Establish connection to the database
        val connection: Connection = DriverManager.getConnection(url, user, password)
        println("🚀 Koneksi ke database berhasil!")

        // Load jadwal from the database
        loadJadwalFromDatabase(connection, jadwalList)

        while (true) {
            println("\n--- Aplikasi Jadwal Perkuliahan ---")
            println("1. Tampilkan Jadwal")
            println("2. Tambah Jadwal")
            println("3. Keluar")
            print("Pilih menu (1-3): ")

            when (scanner.nextLine()) {
                "1" -> {
                    println("\n-- Daftar Jadwal Perkuliahan --")
                    if (jadwalList.isEmpty()) {
                        println("❌ Belum ada jadwal yang ditambahkan.")
                    } else {
                        println("%-5s %-10s %-20s %-10s %-10s %-10s %-10s %-15s".format(
                            "ID", "Kelas", "Nama Mata Kuliah", "Hari", "Mulai", "Selesai", "Ruangan", "Dosen"
                        ))
                        println("-".repeat(100))
                        for (jadwal in jadwalList) {
                            println("%-5s %-10s %-20s %-10s %-10s %-10s %-10s %-15s".format(
                                jadwal.idJadwal, jadwal.kelas, jadwal.namaMataKuliah, jadwal.hari, jadwal.jamMulai,
                                jadwal.jamSelesai, jadwal.ruangan, jadwal.dosenPengajar
                            ))
                        }
                    }
                }
                "2" -> {
                    println("\n-- Tambah Jadwal Baru --")
                    print("Kelas: ")
                    val kelas = scanner.nextLine()
                    print("Nama Mata Kuliah: ")
                    val namaMataKuliah = scanner.nextLine()
                    print("Hari: ")
                    val hari = scanner.nextLine()
                    print("Jam Mulai (HH:MM): ")
                    val jamMulai = scanner.nextLine()
                    print("Jam Selesai (HH:MM): ")
                    val jamSelesai = scanner.nextLine()
                    print("Ruangan: ")
                    val ruangan = scanner.nextLine()
                    print("Dosen Pengajar: ")
                    val dosenPengajar = scanner.nextLine()

                    // Menambahkan jadwal ke dalam list
                    val jadwal = Jadwal(
                        idJadwal = jadwalList.size + 1,  // Menambahkan ID otomatis berdasarkan ukuran jadwalList
                        kelas = kelas,
                        namaMataKuliah = namaMataKuliah,
                        hari = hari,
                        jamMulai = jamMulai,
                        jamSelesai = jamSelesai,
                        ruangan = ruangan,
                        dosenPengajar = dosenPengajar
                    )
                    jadwalList.add(jadwal)

                    // Simpan jadwal ke database
                    saveJadwalToDatabase(connection, jadwal)

                    println("✅ Jadwal berhasil ditambahkan!")
                }
                "3" -> {
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

// Fungsi untuk menyimpan jadwal ke dalam database
fun saveJadwalToDatabase(connection: Connection, jadwal: Jadwal) {
    try {
        val query = "INSERT INTO jadwal_perkuliahan (kelas, namaMataKuliah, hari, jamMulai, jamSelesai, ruangan, dosenPengajar) VALUES (?, ?, ?, ?, ?, ?, ?)"
        val preparedStatement = connection.prepareStatement(query)

        // Mengisi parameter dari jadwal yang dimasukkan
        preparedStatement.setString(1, jadwal.kelas)
        preparedStatement.setString(2, jadwal.namaMataKuliah)
        preparedStatement.setString(3, jadwal.hari)
        preparedStatement.setString(4, jadwal.jamMulai)
        preparedStatement.setString(5, jadwal.jamSelesai)
        preparedStatement.setString(6, jadwal.ruangan)
        preparedStatement.setString(7, jadwal.dosenPengajar)

        // Eksekusi pernyataan SQL
        preparedStatement.executeUpdate()

        println("✅ Jadwal berhasil disimpan ke database.")
    } catch (e: Exception) {
        println("❌ Error inserting to database: ${e.message}")
    }
}

// Fungsi untuk memuat jadwal dari database
fun loadJadwalFromDatabase(connection: Connection, jadwalList: MutableList<Jadwal>) {
    try {
        val query = "SELECT * FROM jadwal_perkuliahan"
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(query)

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
        println("❌ Error loading jadwal from database: ${e.message}")
    }
}