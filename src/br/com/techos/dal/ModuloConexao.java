/*
 * The MIT License
 *
 * Copyright 2022 João Victor Maciel Campos.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Conexão com o banco de dados
 * @author João Victor Maciel Campos
 * @version 0.0.1
 */
package br.com.techos.dal;
import java.sql.*;

public class ModuloConexao {
    /**
     * Método responsavel pela coexão com o banco de dados
     * @return conexao
     */
    //estabelecer conexão com o banco de dados
    public static Connection conector(){
        Connection conexao = null;
        //o comando abaxio ira chamar o driver importado na biblioteca
        String driver = "com.mysql.cj.jdbc.Driver";
        //variaveis para armazenar informações referente ao banco
        /*String url = "jdbc:mysql://192.168.5.101:3306/dbcitech?characterEncoding=utf-8";
        String user = "dba";
        String password = "Jvmacielc08";*/
        String url = "jdbc:mysql://localhost:3306/dbcitech";
        String user = "root";
        String password = "joaovmc98";
        //estabeleendo a onexao com o banco
        try {
            Class.forName(driver); //executa a conexao do driver
            conexao = DriverManager.getConnection(url, user, password);//estabelece a conexao baseado nos parametros
            return conexao;
        } catch (Exception e) {
            //a linha abaixo serve de apoio para esclarecer erros
            System.out.println(e);
            return null;
        }
    }
}
