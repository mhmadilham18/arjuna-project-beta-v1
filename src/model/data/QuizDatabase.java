package model.data;
import model.CharacterType;
import java.util.*;

public class QuizDatabase {
    private static QuizDatabase instance;
    private Map<CharacterType, List<QuizQuestion>> quizzesByCharacter;
    private Random random;

    private QuizDatabase(){
        quizzesByCharacter = new HashMap<>();
        random = new Random();
        initializeQuizzes();
    }

    public static QuizDatabase getInstance(){
        if(instance == null){
            instance = new QuizDatabase();
        }
        return instance;
    }

    private void initializeQuizzes() {
        // Quiz untuk Cakil
        List<QuizQuestion> cakilQuizzes = new ArrayList<>();
        cakilQuizzes.add(new QuizQuestion(1, "Apa sifat utama Cakil dalam pewayangan?", "Tenang", "Licik dan agresif", "Bijaksana", 'B'));
        cakilQuizzes.add(new QuizQuestion(2, "Senjata khas Cakil adalah?", "Keris", "Pedang besar", "Panah", 'A'));
        cakilQuizzes.add(new QuizQuestion(3, "Cakil biasanya berperan sebagai?", "Punakawan", "Raksasa pengganggu", "Raja besar", 'B'));
        cakilQuizzes.add(new QuizQuestion(4, "Karakter Cakil identik dengan?", "Wajah datar", "Gigi menonjol", "Mata sipit", 'B'));
        cakilQuizzes.add(new QuizQuestion(5, "Gerakan Cakil dalam tari sering digambarkan?", "Lembut", "Kaku", "Lincah dan liar", 'C'));
        cakilQuizzes.add(new QuizQuestion(6, "Dalam lakon klasik, Cakil biasanya berhadapan dengan?", "Pendeta", "Ksatria", "Petani", 'B'));
        cakilQuizzes.add(new QuizQuestion(7, "Cakil sering digambarkan sebagai?", "Penjaga kerajaan", "Penjahat yang sombong", "Penyembuh", 'B'));
        cakilQuizzes.add(new QuizQuestion(8, "Warna kostum Cakil sering didominasi oleh?", "Putih", "Merah dan emas", "Biru muda", 'B'));
        cakilQuizzes.add(new QuizQuestion(9, "Ciri tubuh Cakil adalah?", "Berperut buncit", "Kecil namun lincah", "Sangat tinggi", 'B'));
        cakilQuizzes.add(new QuizQuestion(10, "Karakter Cakil terkenal karena?", "Kesetiaan", "Keberanian melawan kebaikan", "Tingkah jenaka", 'C'));
        cakilQuizzes.add(new QuizQuestion(11, "Dalam pertarungan, Cakil biasanya?", "Kalah oleh ksatria", "Menang telak", "Bersekutu dengan ksatria", 'A'));
        cakilQuizzes.add(new QuizQuestion(12, "Ekspresi wajah Cakil digambarkan?", "Garang dan licik", "Sedih", "Tanpa ekspresi", 'A'));
        cakilQuizzes.add(new QuizQuestion(13, "Cakil sering muncul dalam adegan?", "Perang", "Upacara pernikahan", "Sabung ayam", 'A'));
        cakilQuizzes.add(new QuizQuestion(14, "Gaya bicara Cakil biasanya?", "Hormat", "Kasar", "Pelan", 'B'));
        cakilQuizzes.add(new QuizQuestion(15, "Cakil berasal dari golongan?", "Raksasa (Rakshasa)", "Dewa", "Manusia", 'A'));
        cakilQuizzes.add(new QuizQuestion(16, "Peran Cakil dalam cerita adalah untuk?", "Menguji ksatria", "Menjaga hutan", "Melatih ksatria", 'A'));
        cakilQuizzes.add(new QuizQuestion(17, "Cakil sering dikalahkan oleh tokoh?", "Arjuna", "Antareja", "Sumantri", 'A'));
        cakilQuizzes.add(new QuizQuestion(18, "Sikap khas Cakil ketika menyerang?", "Langsung frontal", "Mengintai terlebih dahulu", "Menari-nari dulu", 'C'));
        cakilQuizzes.add(new QuizQuestion(19, "Dalam wayang kulit, suara Cakil biasanya?", "Dalam", "Serak dan melengking", "Berat", 'B'));
        cakilQuizzes.add(new QuizQuestion(20, "Ketika kalah, Cakil biasanya?", "Menghilang", "Menangis", "Terjatuh dengan dramatis", 'C'));
        quizzesByCharacter.put(CharacterType.CAKIL, cakilQuizzes);

        // Quiz untuk Patih Sabrang
        List<QuizQuestion> sabrangQuizzes = new ArrayList<>();
        sabrangQuizzes.add(new QuizQuestion(1, "Patih Sabrang adalah bawahan dari?", "Prabu Ngalengka", "Prabu Sabrang", "Prabu Dasamuka (Rahwana)", 'C'));
        sabrangQuizzes.add(new QuizQuestion(2, "Peran utama Patih Sabrang dalam cerita?", "Penasehat kerajaan", "Panglima perang", "Petani sakti", 'B'));
        sabrangQuizzes.add(new QuizQuestion(3, "Watak Patih Sabrang?", "Setia dan garang", "Malas", "Penakut", 'A'));
        sabrangQuizzes.add(new QuizQuestion(4, "Senjata khas Patih Sabrang?", "Kapak", "Tombak", "Cangkul", 'B'));
        sabrangQuizzes.add(new QuizQuestion(5, "Patih Sabrang bertugas untuk?", "Menjaga dapur kerajaan", "Memimpin prajurit", "Menghibur raja", 'B'));
        sabrangQuizzes.add(new QuizQuestion(6, "Patih Sabrang dikenal sebagai?", "Pejuang tangguh", "Tukang gosip", "Penyihir", 'A'));
        sabrangQuizzes.add(new QuizQuestion(7, "Dalam perang, Patih Sabrang sering melawan?", "Pandawa", "Dewa", "Punakawan", 'A'));
        sabrangQuizzes.add(new QuizQuestion(8, "Postur Patih Sabrang biasanya?", "Tinggi besar", "Kecil", "Sangat kurus", 'A'));
        sabrangQuizzes.add(new QuizQuestion(9, "Warna pakaian Patih Sabrang biasanya?", "Emas dan hijau", "Merah gelap", "Putih bersih", 'B'));
        sabrangQuizzes.add(new QuizQuestion(10, "Patih Sabrang berasal dari kerajaan?", "Alengka", "Amarta", "Dwaraka", 'A'));
        sabrangQuizzes.add(new QuizQuestion(11, "Gaya bicara Patih Sabrang?", "Tegas", "Pelan", "Menggumam", 'A'));
        sabrangQuizzes.add(new QuizQuestion(12, "Ciri khas wajah Patih Sabrang?", "Tersenyum", "Murka", "Kosong", 'B'));
        sabrangQuizzes.add(new QuizQuestion(13, "Dia dikenal sebagai patih yang?", "Tidak disiplin", "Sangat disiplin", "Jarang muncul", 'B'));
        sabrangQuizzes.add(new QuizQuestion(14, "Lawan terberat Patih Sabrang dalam banyak lakon?", "Nakula", "Bima", "Sengkuni", 'B'));
        sabrangQuizzes.add(new QuizQuestion(15, "Sifat Patih Sabrang terhadap rajanya?", "Sangat setia", "Tidak peduli", "Pengkhianat", 'A'));
        sabrangQuizzes.add(new QuizQuestion(16, "Dalam komando perang, Patih Sabrang bersifat?", "Ragu-ragu", "Langsung memerintah", "Mencari alasan", 'B'));
        sabrangQuizzes.add(new QuizQuestion(17, "Dalam mitologi, Patih Sabrang termasuk golongan?", "Ksatria Alengka", "Raksasa Alengka", "Punakawan", 'B'));
        sabrangQuizzes.add(new QuizQuestion(18, "Ketika menghadapi musuh kuat, Patih Sabrang biasanya?", "Mundur", "Sabar lalu menyerang", "Berteriak minta bantuan", 'B'));
        sabrangQuizzes.add(new QuizQuestion(19, "Kekuatan fisik Patih Sabrang digambarkan?", "Lemah", "Sedang", "Sangat kuat", 'C'));
        sabrangQuizzes.add(new QuizQuestion(20, "Akhir dari banyak kisah Patih Sabrang?", "Menang selalu", "Gugur dalam perang", "Menjadi raja", 'B'));
        quizzesByCharacter.put(CharacterType.PATIH_SABRENG, sabrangQuizzes);
    }

    public QuizQuestion getRandom(CharacterType characterType){
        List<QuizQuestion> quizzes = quizzesByCharacter.get(characterType);
        if ((quizzes == null) || quizzes.isEmpty()){
            return null;
        }
        return quizzes.get(random.nextInt(quizzes.size()));
    }
}
