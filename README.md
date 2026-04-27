# booklibrary

書籍と著者を管理する REST API。QUO CARD Digital Innovation Lab コーディングテスト
([課題](https://quo-digital.hatenablog.com/entry/2024/03/22/143542)) の提出物です。

## 技術スタック

- Kotlin 2.2.21 / Java 17
- Spring Boot 4.0.5 (web / jOOQ / Flyway)
- jOOQ 3.19.31 (KotlinGenerator) / PostgreSQL 17
- Gradle 9.4.1
- テスト: JUnit 5 / MockK / Testcontainers
- Lint: ktlint 14.2.0

## 必要環境

- JDK 17 (`.sdkmanrc` で `17.0.13-tem` を指定)
- Kotlin 2.2.21 (`.sdkmanrc` で固定)
- Docker (compose で PostgreSQL を起動)

`sdk env` を打てば `.sdkmanrc` 通りに JDK / Kotlin が切り替わります。

## 起動手順

```sh
# 1. PostgreSQL を起動 (TZ = Asia/Tokyo)
docker compose up -d postgres

# 2. マイグレーション + jOOQ コード生成
./gradlew flywayMigrate jooqCodegen

# 3. アプリ起動 (http://localhost:8080)
./gradlew bootRun
```

`bootRun` は `spring-boot-docker-compose` 経由で compose も自動起動できますが、
初回は `jooqCodegen` が DB を必要とするため `docker compose up` を先に走らせる
方が安全です。

## エンドポイント

| Method | Path | 概要 | 主なステータス |
|---|---|---|---|
| POST   | `/authors`            | 著者を登録            | 201 / 400 |
| PATCH  | `/authors/{id}`       | 著者を部分更新        | 200 / 400 / 404 |
| GET    | `/authors/{id}/books` | 著者の書籍一覧を取得  | 200 / 404 |
| POST   | `/books`              | 書籍を登録            | 201 / 400 / 422 |
| PATCH  | `/books/{id}`         | 書籍を部分更新        | 200 / 400 / 404 / 409 / 422 |

ステータスコードの使い分け:

- `400`: DTO バリデーション違反 (blank / 範囲外 / 全フィールド null PATCH など)
- `404`: パスの id 自体が存在しない
- `409`: 出版済み → 未出版への遷移 (`BusinessRuleException`)
- `422`: 存在しない `authorId` を含む書籍登録/更新 (`UnknownAuthorException`)

## curl 例

```sh
# 著者登録
curl -X POST http://localhost:8080/authors \
  -H 'Content-Type: application/json' \
  -d '{"name":"夏目漱石","birthDate":"1867-02-09"}'
# レスポンス例: {"id":1,"name":"夏目漱石","birthDate":"1867-02-09"}

# 書籍登録
curl -X POST http://localhost:8080/books \
  -H 'Content-Type: application/json' \
  -d '{"title":"吾輩は猫である","price":800,"publicationStatus":"PUBLISHED","authorIds":[1]}'
# レスポンス例: {"id":1,"title":"吾輩は猫である","price":800,"publicationStatus":"PUBLISHED","authorIds":[1]}

# 著者を部分更新
curl -X PATCH http://localhost:8080/authors/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"夏目 金之助"}'

# 書籍を部分更新 (価格のみ)
curl -X PATCH http://localhost:8080/books/1 \
  -H 'Content-Type: application/json' \
  -d '{"price":900}'

# 著者の書籍一覧
curl http://localhost:8080/authors/1/books
```

## テスト

クリーン環境では、テスト実行時も jOOQ コード生成のために PostgreSQL が必要です。

```sh
docker compose up -d postgres

./gradlew test          # 全テスト (Repository テストは Testcontainers で実 PG を起動)
./gradlew ktlintCheck   # スタイルチェック
./gradlew check         # test + ktlint
```

層ごとのテスト方針:

| 層 | 起動 | DB | 上流の扱い |
|---|---|---|---|
| Domain / DTO / Service / Controller / GlobalExceptionHandler | 素 JUnit | なし | MockK で stub (Service / Controller のみ) |
| Repository | `@JooqTest` | Testcontainers (実 PostgreSQL) | — |

## ディレクトリ構成

```
src/
├── main/kotlin/com/katsu/booklibrary/
│   ├── BooklibraryApplication.kt
│   ├── controller/   ... REST エンドポイント
│   ├── service/      ... ビジネスルール (出版状態の遷移など)
│   ├── repository/   ... jOOQ ベースの DB アクセス
│   ├── domain/       ... Author / Book (init 不変条件)
│   ├── dto/          ... Request / Response (init 境界検証)
│   └── exception/    ... ApiExceptions / GlobalExceptionHandler
├── main/resources/
│   ├── application.yml
│   └── db/migration/V1__init.sql
└── test/kotlin/com/katsu/booklibrary/
    └── (各層に対応するテスト + support/PostgresContainerBase.kt)
```

jOOQ 生成コードは `build/generated-src/jooq/main/` に出力され、コミット対象外です。
