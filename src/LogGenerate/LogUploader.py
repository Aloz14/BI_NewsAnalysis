import paramiko


class LogUploader:
    def __init__(self, host, port, username, password):
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.ssh_client = paramiko.SSHClient()
        self.sftp_client = None

    def connect(self):
        self.ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        self.ssh_client.connect(hostname=self.host, port=self.port, username=self.username, password=self.password)
        self.sftp_client = self.ssh_client.open_sftp()

    def write_data_to_file(self, data, remote_file_path):
        if not self.sftp_client:
            raise Exception('SFTP client is not connected. Call connect() method first.')
        with self.sftp_client.file(remote_file_path, 'w') as file:
            file.write(data.to_json())
        print('数据成功写入到服务器文件')

    def close(self):
        if self.sftp_client:
            self.sftp_client.close()
        if self.ssh_client:
            self.ssh_client.close()