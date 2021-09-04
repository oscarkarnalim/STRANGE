# STRANGE: Similarity TRacker in Academia with Natural lanGuage Explanation

**STRANGE** \(Similarity TRacker in Academia with Natural lanGuage Explanation\) is a tool for observing similarities and the surface differences among Java/Python source code files. For Python formatted layout,  Python compiler needs to be installed and its command should be recognised globally in the command prompt. Otherwise, the absolute path should be set in 'static_setting.txt'. Further details can be seen in [the corresponding paper](https://doi.org/10.1109/ACCESS.2021.3073703) published in IEEE Access. You can see this guideline in Indonesian at the end of this file. The comparison algorithm (RKRGST) has been updated since its first publication for efficiency.

<p align="center">
<img width="80%" src="https://github.com/oscarkarnalim/strange/blob/main/strange_main_layout.png?raw=true">
</p>
 
## STRANGE Modes 
### 1. Pair observation
This mode captures the similarities shared between two given source code files and stores them as an interactive HTML page. This mode can be embedded when developing other similarity detection software. 

*Quick execution command*:
```
pair <codepath1> <codepath2> <programming_language> <target_html_filepath>
```
*Complete execution command*: 
```
pair <codepath1> <codepath2> <programming_language> <target_html_filepath> <projectname1> <projectname2> <home_filepath> <syntax_minimum_match_length> <additional_keywords_path>
```
### 2. Assignment comparison
This mode pairwise compares student code files for an assignment and shows the similarities in interactive HTML pages. It accepts an assignment directory with each sub-directory represents a student submission containing one code file. The results are stored as a directory with 'index.html' as the entry page. Please preprocess the assignment directory with mode 4 if the assignment is broken down to sub-assignments, or with mode 5 if the assignment requires more than one code file per submission. 

*Quick execution command*: 
```
comp <assignment_root_dirpath> <programming_language> <target_html_dirpath>
```
*Complete execution command*: 
```
comp <assignment_root_dirpath> <programming_language> <target_html_dirpath> <sim_threshold> <syntax_minimum_match_length> <additional_keywords_path>
```
### 3. JPlag result update
This mode upgrades JPlag-style HTML pages for similarity observation with the ones from STRANGE. It accepts a JPlag-generated directory and an assignment directory with each sub-directory represents a student submission containing one file. Each of the HTML pages will be replaced with the STRANGE-style ones. 
*Quick execution command*: 
```
jplag <jplag_root_dirpath> <assignment_root_dirpath> <programming_language>
```
*Complete execution command*: 
```
jplag <jplag_root_dirpath> <assignment_root_dirpath> <programming_language> <syntax_minimum_match_length> <additional_keywords_path>
```
### 4. Assignment code grouping
This mode groups student submissions based on their sub-assignments' filename patterns. It accepts an assignment directory with each sub-directory represents a student submission containing several code files, and then group the code files based on the sub-assignments' filename patterns. The results will be stored in a directory named '\[grouped\]' + assignment directory name. Each sub-assignment will be represented as one sub-directory in which the name is based on the pattern but with only alphanumerics. Those that do not share one of the given file name pattern will be grouped as 'uncategorised'. 

*Execution command*: 
```
group <assignment_root_dirpath> <programming_language> <name_pattern_1> <name_pattern_2> ... <name_pattern_N> 
```
### 5. Assignment code file merging
This mode merges all code files in each student submission. It accepts an assignment directory containing students projects as the sub-directories, and then merge each project's code files as one large code file. The results will be stored in a directory named '\[merged\]' + assignment directory name, where each sub-directory represents one student project.

*Execution command*: 
```
merge <assignment_root_dirpath> <programming_language>
```
### 6. Template code removal
This mode removes template code from all code files in an assignment directory. It accepts an assignment directory and a template code file. The results will be stored in a directory named '\[template removed\]' + assignment directory name, where the directory structure is similar to the given assignment directory, except that only processed    code files are copied. The removed template code will be informed per code file as comments. Template code is detected with a greedy algorithm and it is not guarantee to be completely accurate (despite it is still acceptable).

*Execution command*: 
```
template <assignment_root_dirpath> <programming_language> <template_path>
```
## Parameters description
### Mode-1 \('pair'\) exclusive parameters description
  *\<codepath1\>*: a string representing the file path for the first code. Please use quotes if the path contains spaces. 
  
  *\<codepath2\>*: a string representing the file path for the second code. Please use quotes if the path contains spaces. 
  
  *\<home_filepath\>*: a string representing the file path for home html page \(i.e., the HTML page displayed before accessing the resulted page\). Enter null if no such a page exists. Please use quotes if the path contains spaces. This parameter is null by default. 
  
  *\<projectname1\>*: a string representing the project name for the first code. This can be used to distinguish given code files if they share the same name. This is assigned to *\<codepath1\>* by default. 
  
  *\<projectname2\>*: a string representing the project name for the second code. This can be used to distinguish given code files if they share the same name. This is assigned to *\<codepath2\>* by default. 
  
  *\<target_html_filepath\>*: a string representing the file path for the generated HTML. Please use quotes if the path contains spaces. 

### Mode-2 \('comp'\) exclusive parameters description
  *\<sim_threshold\>*: a floating number representing the threshold of suspicion. Only source code pairs with similarity degree greater than or equal to the threshold are displayed in the results. This is assigned with 0 by default. Value: a floating number between 0 to 1 \(inclusive\). 
  
  *\<target_html_dirpath\>*: a string representing the directory path for the generated HTML pages. Please use quotes if the path contains spaces. 

### Mode-3 \('jplag'\) exclusive parameters description
  *\<jplag_root_dirpath\>*: a string representing the directory path of JPlag result. Please use quotes if the path contains spaces.

### Mode-4 \('group'\) exclusive parameters description
  *\<name_pattern\>*: a string representing the name pattern of a sub-assignment, written as Java regular expression. Please use quotes if the path contains spaces.

### Mode-6 \('template'\) exclusive parameters description
  *\<template_path\>*: a string representing a file containing template code that will be removed. The template code should be written in compliance to the programming language's syntax.

### Shared parameters description
  *\<additional_keywords_path\>*: a string representing a file containing additional keywords with newline as the delimiter. Keywords with more than one token should be written by embedding spaces between the tokens. For example, 'System.out.print' should be written as 'System . out . print'. Set this parameter to null if not needed. 
  
  *\<assignment_root_dirpath\>*: a string representing the assignment root directory. That directory should contain directories in which each of them refers to one student program and has one Java or Python code file. Please use quotes if the path contains spaces. 
  
  *\<programming_language\>*: a constant depicting the programming language used on given source code files. Value: 'java' \(for Java\) or 'py' \(for Python\). 
  
  *\<syntax_minimum_match_length\>*: a number depicting the lowest length of captured similar syntax code fragments. This is assigned 2 by default. Value: a positive integer. 


## Acknowledgments
This tool uses [ANTLR](https://www.antlr.org/) to tokenise given programs, [Apache Lucene](https://lucene.apache.org/) to identify stop words, [Google Prettify](https://github.com/google/code-prettify) to display source code, [google-java-format](https://github.com/google/google-java-format) to reformat Java code, [YAPF](https://github.com/google/yapf) to reformat Python code, and [JSoup](https://jsoup.org/) to parse JPlag's index page. It also adapts [arunjeyapal's implementation of RKR-GST](https://github.com/arunjeyapal/GreedyStringTiling) and [AayushChaturvedi's implementation of string alignment](https://www.geeksforgeeks.org/sequence-alignment-problem/).

# Indonesian guideline for STRANGE
**STRANGE** \(Similarity TRacker in Academia with Natural lanGuage Explanation\) adalah sebuah kakas untuk mengamati kesamaan dan perbedaan level permukaan dari kode sumber Java/Python. Untuk Python, kompilernya perlu diinstall dan perintah ekseksuisnya harus dikenali secara global di command prompt. Jika tidak, path absolutnya harus diset di 'static_setting.txt'. Detail lebih jauh dapat dilihat di [artikel terkait](https://doi.org/10.1109/ACCESS.2021.3073703) yang dipublikasikan di IEEE Access. Algoritma perbandingan (RKRGST) sudah diupdate lebih jauh dari versi awalnya untuk efisiensi. You can see this guideline in English at the beginning of the file.


## Mode STRANGE
### 1. Pengamatan pasangan kode sumber
Menangkap kesamaan dari dua kode sumber dan menyimpannya dalam sebuah laman interaktif HTML. Mode ini dapat diintegrasikan dalam pengembangan kakas deteksi kesamaan lainnya.

*Eksekusi standar*: 
```
pairi <codepath1> <codepath2> <programming_language> <target_html_filepath>
```
*Eksekusi komprehensif*:
```
pairi <codepath1> <codepath2> <programming_language> <target_html_filepath> <projectname1> <projectname2> <home_filepath> <syntax_minimum_match_length>  <additional_keywords_path>
```

### 2. Perbandingan sekumpulan program
Membandingkan sekumpulan kode siswa untuk sebuah tugas dan menampilkan kesamaannya dalam laman HTML interaktif. Mode ini menerima sebuah direktori tugas dengan setiap upa direktorinya merepresentasikan satu tugas mahasiswa dan berisi satu file kode. Hasil akan disimpan dalam sebuah direktori dengan 'index.htnl' sebagai laman awal. Mohon memproses direktori tugas dengan mode 4 terlebih dahulu jika tugas tersebut terdiri dari banyak upa tugas, atau mode 5 jika tugas tersebut mewajibkan lebih dari satu file kode solusi untuk setiap siswa.

*Eksekusi standar*: 
```
compi <assignment_root_dirpath> <programming_language> <target_html_dirpath>
```
*Eksekusi komprehensif*:
```
compi <assignment_root_dirpath> <programming_language> <target_html_dirpath> <sim_threshold> <syntax_minimum_match_length> <additional_keywords_path>
```


### 3. Modifikasi hasil JPlag
Mengganti laman HTML untuk observasi kesamaan dari JPlag dengan laman HTML serupa dari STRANGE. Mode ini menerima direktori output JPlag dan sebuah   direktori tugas. Setiap laman HTMLnya akan digantikan dengan HTML serupa dari STRANGE.

*Eksekusi standar*:
```
jplagi <jplag_root_dirpath> <assignment_root_dirpath> <programming_language>
```
*Eksekusi komprehensif*: 
```
jplagi <jplag_root_dirpath> <assignment_root_dirpath> <programming_language> <syntax_minimum_match_length> <additional_keywords_path>
```

### 4. Pengelompokan kode tugas
Mengelompokkan tugas siswa berdasarkan pola nama file upa tugas. Mode ini menerima sebuah direktori tugas dengan setiap upa direktori merepresentasikan satu tugas mahasiswa, dan mengelompokkan kode berdasarkan pola nama upa tugas dalam sebuah direktori upa tugas dalam sebuah direktori bernama '[grouped]' + nama direktori tugas. Setiap upa tugas akan direpresentasikan dengan sebuah upa direktori dengan nama seperti pola namanya namun hanya melibatkan alphanumerik. Kode yang tidak memenuhi pola akan dikategorikan sebagai 'uncategorised'.

*Perintah eksekusi*: 
```
groupi <assignment_root_dirpath> <programming_language> <name_pattern_1> <name_pattern_2> ... <name_pattern_N> 
```

### 5. Penggabungan file kode tugas
Menggabungkan semua file kode dari tiap tugas mahasiswa. Mode ini menerima direktori tugas dengan upa direktorinya merupakan proyek siswa, dan untuk setiap proyek, menggabungkan semua kode sumbernya menjadi sebuah file kode besar. Hasil diletakkan di direktori bernama '[merged]' + nama direktori tugas, dimana setiap upa direktori merepresentasikan satu proyek.

*Perintah eksekusi*: 
```
mergei <assignment_root_dirpath> <programming_language>
```

### 6. Penghapusan kode template
Menghapus kode template dari setiap file kode di direktori tugas. Mode ini menerima direktori tugas dan sebuah file kode template. Hasil akan diletakkan di direktori bernama '[template removed]' + nama direktori tugas, dimana  struktur direktorinya akan sama seperti direktori tugas masukan kecuali hanya berisi file kode terproses. Kode template terhapus akan diinformasikan di setiap file kode dalam bentuk komentar. Deteksi kode template dilakukan dengan algoritma greedy dan tidak menjamin hasil sempurna (walaupun masih dapat diterima).

*Perintah eksekusi*: 
```
templatei <assignment_root_dirpath> <programming_language> <template_path>
```


## Deskripsi parameter 
### Deskripsi parameter ekslusif mode-1 ('pairi')
  *\<codepath1\>*: string berisi path file kode pertama. Mohon gunakan kutip jika path mengandung spasi.
  
  *\<codepath2\>*: string berisi path file kode kedua. Mohon gunakan kutip jika path mengandung spasi.
  
  *\<home_filepath\>*: string berisi path file dari laman html utama (laman yang ditampilkan sebelum mengakses laman hasil). Isi dengan null jika tidak ada laman utama. Mohon gunakan kutip jika path mengandung spasi. Parameter ini diisi null secara otomatis.
  
  *\<projectname1\>*: string berisi nama proyek untuk kode pertama. Ini dapat digunakan untuk membedakan kode dengan nama file sama. Parameter ini diisi dengan *\<codepath1\>* secara otomatis.
  
  *\<projectname2\>*: string berisi nama proyek untuk kode kedua. Ini dapat digunakan untuk membedakan kode dengan nama file sama. Parameter ini diisi dengan *\<codepath2\>* secara otomatis.
  
  *\<target_html_filepath\>*: string berisi path file untuk lokasi laman hasil. Mohon gunakan kutip jika path mengandung spasi.

### Deskripsi parameter eksklusif mode-2 ('compi')
  *\<sim_threshold\>*: bilangan riil yang menyatakan batas minimum kesamaan dalam mencurigai pasangan kode sumber. Pasangan dengan kesamaan melebihi atau sama dengan batas akan ditampilkan di laman luaran. Parameter ini diisi 0 secara otomatis. Nilai: bilangan riil diantara 0 hingga 1 secara inclusif.
  
  *\<target_html_dirpath\>*: string berisi path direktori untuk lokasi direktori hasil. Mohon gunakan kutip jika path mengandung spasi.

### Deskripsi parameter eksklusif mode-3 ('jplagi')
  *\<jplag_root_dirpath\>*: string berisi path direktori hasil luaran JPlag. Mohon gunakan kutip jika path mengandung spasi.

### Deskripsi parameter eksklusif mode-4 ('groupi')
  *\<name_pattern\>*: string berisi pola nama dari satu upa tugas, ditulis mengikuti kaidah regular expression Java. Mohon gunakan kutip jika pola mengandung spasi.

### Deskripsi parameter eksklusif mode-6 ('templatei')
  *\<template_path\>*: string berisi path file untuk kode template yang akan dibuang. Kode template dituliskan sesuai dengan sintaks bahasa pemrograman yang digunakan.

### Deskripsi parameter lainnya
  *\<additional_keywords_path\>*: string berisi path file untuk kata kunci pemrograman tambahan dengan baris baru sebagai pemisah kata kunci. Kata kunci yang melibatkan lebih dari satu token harus ditulis dalam satu baris dengan spasi sebagai pemisah antar token. Sebagai contoh, 'System.out.print' dapat ditulis sebagai 'System . out . print'. Isi parameter ini dengan null jika tidak diperlukan.
  
  *\<assignment_root_dirpath\>*: string berisi path direktori dari kumpulan program. Direktori harus berisi beberapa sub-direktori; setiap sub-direktori berisi satu kode sumber dalam bahasa Java atau Python. Mohon gunakan kutip jika path mengandung spasi.
  
  *\<programming_language\>*: konstanta menyatakan bahasa pemrograman kode sumber yang akan diproses. Nilai: java (untuk Java) atau py (untuk Python).
  
  *\<syntax_minimum_match_length\>*: bilangan bulat menyatakan panjang minimum dari sekuens token sama yang akan dikenali oleh sistem. Parameter ini diisi dengan dua secara otomatis. Nilai: bilangan bulat positif.

