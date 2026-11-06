# KitchenDroid

Kitchen ROM tool untuk Android tanpa root. Semua kerjaan (unpack, edit .prop, hapus file) dilakukan di workspace internal aplikasi (`filesDir/workspace`), tidak butuh root dan tidak butuh file manager luar.

## Fitur
- Impor file dari SDcard (melalui sistem picker Android)
- Unpack ZIP / image ke workspace internal
- Edit file `.prop` langsung di aplikasi
- Hapus file di workspace
- 2 bahasa: English & Indonesia
- Desain Material Expressive sederhana, ala iOS

## Struktur penyimpanan
- Workspace (unpack, edit): `/data/data/com.kitchendroid.app/files/workspace/` — internal app, tanpa root
- Input/Output image: `/sdcard/KitchenDroid/[Project]/input` dan `output` — via SAF picker

## Build via GitHub Actions
Push ke branch `main`, GitHub Actions akan otomatis build APK dan menyimpan hasilnya sebagai artifact.

## Build manual di Termux
