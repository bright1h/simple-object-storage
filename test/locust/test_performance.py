import random
from locust import HttpLocust, TaskSet, task

class SimpleTasks(TaskSet):
    object_name = "test.jpg"
    object_md5 = "60d2e411eb2f43968bd63f3ea094999f"
    # Use only 1 part
    # obj_part_1 = open('../resources/test.jpg', 'rb')
    # obj_part_1_size = obj_part_1.tell()
    # obj_part_1 = open('file.pdf', 'rb')

    # @task
    # def get_bucket_detail(self):
    #     # using existed bucket 'test' for testing
    #     self.client.get("/test?list")

    @task
    def create_bucket(self):
        self.client.post(
            "/ptest?create"
        )

    @task
    def create_upload_ticket(self):
        self.client.post(
            "/ptest/"+self.object_name+"?create"
        )

    # @task
    # def upload_part(self):
    #     # print("type:",type(self.obj_part_1_size))
    #     print(self.obj_part_1_size)
    #     response = self.client.put(
    #         "/ptest/"+self.object_name+"?partNumber=1",
    #         headers={"Content-MD5":self.object_md5, "Content-length":str(self.obj_part_1_size)},
    #         files={"":self.obj_part_1}
    #     )

    #     print(response.status_code)
    #     print(response.text)
    
    @task
    def complete(self):
        self.client.post("/ptest/"+self.object_name+"?complete")
    
    @task
    def download_object(self):
        self.client.get("/ptest/"+self.object_name)

    @task
    def update_metadata(self):
        self.client.put("/ptest/"+self.object_name+"?metadata&key=testkey",data="'value' for test key fffffdsfvgdvgdvgdfvdfdf")

    @task
    def get_metadata_by_key(self):
        self.client.get("/ptest/"+self.object_name+"?metadata&key=testkey")

    @task
    def get_all_metadata(self):
        self.client.get("/ptest/"+self.object_name+"?metadata")

    # DELETE Section
    @task
    def delete_metadata(self):
        self.client.delete("/ptest/"+self.object_name+"?metadata&key=testkey")


    @task
    def delete_part(self):
        self.client.delete("/ptest/"+self.object_name+"?partNumber=1")

    @task
    def delete_object(self):
        self.client.delete("/ptest/"+self.object_name+"?delete")

    @task
    def delete_bucket(self):
        self.client.delete("/ptest?delete")

class WebsiteUser(HttpLocust):
    task_set = SimpleTasks
    host = 'http://127.0.0.1:8080'
    min_wait = 1000
    max_wait = 1000
