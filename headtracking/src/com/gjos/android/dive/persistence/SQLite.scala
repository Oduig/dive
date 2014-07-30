package com.gjos.android.dive.persistence

import android.content.Context
import android.database.{DatabaseUtils}
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext.Implicits.global


class SQLite(dbName: String, version: Int, context: Context) extends SQLiteOpenHelper(context, dbName, null, version) {

  private def createUiSettingsTableSql = {
    // Allow only one entry
    """CREATE TABLE IF NOT EXISTS ui_settings (
      |  id INTEGER PRIMARY KEY,
      |  connection_type INTEGER NOT NULL,
      |  ip_address TEXT NOT NULL,
      |  port INTEGER NOT NULL,
      |  bluetooth_address TEXT NOT NULL
      |)
    """.stripMargin
  }

  private def esc(s: String) = DatabaseUtils.sqlEscapeString(s)

  private def saveUiSettingsQuery(settings: UiSettings) = {
    s"""REPLACE INTO ui_settings (id, connection_type, ip_address, port, bluetooth_address)
       |VALUES (1, ${settings.connectionType}, ${esc(settings.ipAddress)}, ${settings.port}, ${esc(settings.btAddress)})
    """.stripMargin
  }

  private def fetchUiSettingsQuery: String = {
    s"""SELECT connection_type, ip_address, port, bluetooth_address
       |FROM ui_settings
       |LIMIT 1""".stripMargin
  }

  def fetchUiSettings(): Future[Option[UiSettings]] = Future {
    val query = fetchUiSettingsQuery
    val cursor = blocking(getReadableDatabase.rawQuery(query, Array.empty))
    if (cursor.moveToNext()) {
      Some(UiSettings(
        cursor.getInt(cursor getColumnIndex "connection_type"),
        cursor.getString(cursor getColumnIndex "ip_address"),
        cursor.getInt(cursor getColumnIndex "port"),
        cursor.getString(cursor getColumnIndex "bluetooth_address")
      ))
    } else {
      None
    }
  }

  def saveUiSettings(settings: UiSettings) = Future {
    val query = saveUiSettingsQuery(settings)
    blocking { getWritableDatabase.execSQL(query) }
  }

  override def onCreate(db: SQLiteDatabase) {
    db.execSQL(createUiSettingsTableSql)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    onCreate(db)
  }

}