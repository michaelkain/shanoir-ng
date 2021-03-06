import time
import os
import argparse
from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import random


parser = argparse.ArgumentParser()
parser.add_argument('-b', '--browser', type=str, choices=['firefox', 'chrome', 'ie'], help='Browser name', required=True)
parser.add_argument('-a', '--address', type=str, help='Shanoir address: ex. \'http://localhost\'  for local or \
                    http://shanoir-ng-users  for docker', required=True)
parser.add_argument('--remote', action='store_true', help='Launch in docker')
parser.add_argument('-u', '--user', type=str, help='User login', required=True)
parser.add_argument('-p', '--password', type=str, help='User password', required=True)
parser.add_argument('-s', '--shanoir', type=str, choices=['users', 'studies'], help='Shanoir to test', required=True)
args = parser.parse_args()
print args


def start_selenium():
    global driver
    b = args.browser
    if args.remote:
        if b == "chrome":
            dc = DesiredCapabilities.CHROME
        else:
            dc = DesiredCapabilities.FIREFOX
            dc['marionette'] = True
        driver = webdriver.Remote(command_executor='http://127.0.0.1:4444/wd/hub', desired_capabilities=dc)
        driver.get(args.address)
    else:
        if b == "ie":
            driver = webdriver.Ie(os.getcwd()+"\IEDriverServer.exe")
        elif b == "chrome":
            driver = webdriver.Chrome(os.getcwd()+"\chromedriver.exe")
        else:
            # Firefox
            fp = webdriver.FirefoxProfile()
            fp.set_preference("browser.startup.homepage_override.mstone", "ignore")
            fp.set_preference("startup.homepage_welcome_url.additional", "about:blank")
            dc = DesiredCapabilities.FIREFOX
            dc['marionette'] = True
            driver = webdriver.Firefox(firefox_profile=fp, capabilities=dc)
        driver.get(args.address)
    # driver.set_window_size(1360, 1020)
    driver.maximize_window()

path_to_downloads = os.getcwd()+"\\downloads\\"
if not os.path.exists(path_to_downloads):
    os.makedirs(path_to_downloads)


def wait_to_be_present_and_click(xpath):
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, xpath)))
    driver.find_element_by_xpath(xpath).click()
    return True


def wait_to_be_clickable_and_click(xpath):
    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH, xpath)))
    driver.find_element_by_xpath(xpath).click()
    return True


def wait_and_send_keys(xpath, text):
    WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, xpath)))
    driver.find_element_by_xpath(xpath).send_keys(text)
    return True


def wait_to_be_visible(xpath):
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, xpath)))
    return True


def clear_input(xpath):
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, xpath)))
    driver.find_element_by_xpath(xpath).clear()
    return driver.find_element_by_xpath(xpath).get_attribute("value") == ""


def get_innertext(xpath):
    # TODO: doesn't work in firefox
    return driver.find_element_by_xpath(xpath).get_attribute("innerText")


def count_elements(xpath):
    return len(driver.find_elements_by_xpath(xpath))


def check_for_text_in_elements(text, xpath):
    for i in driver.find_elements_by_xpath(xpath):
        print i.get_attribute("innerText")
        if text in i.get_attribute("innerText"):
            return True
    return False


def check_if_shanoir_table_has_rows():
    table_xpath = "//shanoir-table//tr[@class='even']"
    wait_to_be_visible(table_xpath)
    return count_elements(table_xpath) > 0


def login(user, password):
    wait_and_send_keys("//input[@id='username']", user)
    wait_and_send_keys("//input[@id='password']", password)

    time.sleep(1)
    wait_to_be_clickable_and_click("//button[@name='login']")

    welcome_xpath = "//div[@class='header-component']"
    wait_to_be_visible(welcome_xpath)
    time.sleep(1)
    assert "Welcome" in get_innertext(welcome_xpath)


def logout():
    wait_to_be_clickable_and_click("//button[contains(.,'Logout')]")

    wait_to_be_visible("//input[@id='username']")
    assert count_elements("//div[contains(.,'Connect to Shanoir')]") > 0


def manage_users():
    button_admin_xpath = "//span[contains(.,'Administration')]"
    wait_to_be_clickable_and_click(button_admin_xpath)

    button_userlist_xpath = "//span[contains(.,'Manage users')]"
    wait_to_be_clickable_and_click(button_userlist_xpath)
    assert check_if_shanoir_table_has_rows()


def manage_centers():
    button_manage_data_xpath = "//span[contains(.,'Manage data')]"
    wait_to_be_clickable_and_click(button_manage_data_xpath)

    button_center_xpath = "//span[contains(.,'Center')]"
    wait_to_be_clickable_and_click(button_center_xpath)
    assert check_if_shanoir_table_has_rows()


def manage_acq_eq():
    button_manage_data_xpath = "//span[contains(.,'Manage data')]"
    wait_to_be_clickable_and_click(button_manage_data_xpath)

    button_acq_eq_xpath = "//menu-item[@label='Acquisition equipment']"
    wait_to_be_clickable_and_click(button_acq_eq_xpath)
    assert check_if_shanoir_table_has_rows()


def pink_mode():
    # Get the best color
    wait_to_be_clickable_and_click("//span[contains(.,'Administration')]")
    wait_to_be_clickable_and_click("//span[contains(.,'Preferences')]")
    wait_to_be_clickable_and_click("//span[contains(.,'Pink mode')]")
    wait_to_be_clickable_and_click("//span[contains(.,'Administration')]")


def search(search_string, select_option):
    input_search_xpath = "//span[@class='text-search']/input[contains(@class,'search-txt')]"
    wait_and_send_keys(input_search_xpath, search_string)

    option_role_xpath = "//span[@class='text-search']//option[contains(.,'"+select_option+"')]"
    time.sleep(1)
    wait_to_be_clickable_and_click(option_role_xpath)

    time.sleep(1)

    assert check_if_shanoir_table_has_rows()
    assert check_for_text_in_elements(search_string, "//shanoir-table//tbody/tr")


def clean_search():
    button_clean_xpath = "//span[@class='text-search']/button"
    wait_to_be_clickable_and_click(button_clean_xpath)
    assert check_if_shanoir_table_has_rows()


def add_user():
    random_int = random.randint(1000, 9999)
    role = "User"
    first_name = "test1"
    last_name = "test2"
    email = "testusername"+str(random_int)+"@shanoir.fr"
    expiration_date = "01/01/2025"

    wait_to_be_present_and_click("//span[contains(.,'new user')]")

    first_name_xpath = "//input[@id='firstName']"
    last_name_xpath = "//input[@id='lastName']"
    email_xpath = "//input[@id='email']"
    expiration_date_xpath = "//input[@aria-label='Date input field']"
    option_role_xpath = "//select[@id='role']/option[contains(.,'" + role + "')]"

    # Fill in the fields
    wait_to_be_visible(option_role_xpath)

    driver.find_element_by_xpath(first_name_xpath).send_keys(first_name)
    driver.find_element_by_xpath(last_name_xpath).send_keys(last_name)
    driver.find_element_by_xpath(email_xpath).send_keys(email)
    time.sleep(1)
    driver.find_element_by_xpath(expiration_date_xpath).send_keys(expiration_date)
    driver.find_element_by_xpath(option_role_xpath).click()

    # Submit
    submit_xpath = "//button[@type='submit']"
    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH, submit_xpath)))
    time.sleep(1)
    driver.find_element_by_xpath(submit_xpath).click()

    return email


def edit_user(name):
    wait_to_be_present_and_click("//tr[td[contains(.,'"+name+"')]]//a[contains(@href,'editUser')]")
    wait_and_send_keys("//input[@id='email']", "edit")
    wait_to_be_clickable_and_click("//button[@type='submit']")


def delete_user(t):
    wait_to_be_present_and_click("//tr[td[contains(.,'"+t+"')]]//img[contains(@src,'garbage')]")
    wait_to_be_clickable_and_click("//button[contains(.,'OK')]")


def request_account():
    link_create_account_xpath = "//a[contains(.,'Create an account')]"
    wait_to_be_clickable_and_click(link_create_account_xpath)

    random_int = random.randint(1000, 9999)
    first_name = "test_account"
    last_name = "test_account"
    email = "testusername"+str(random_int)+"@shanoir.fr"
    request_inputs = "test"

    first_name_xpath = "//input[@id='firstName']"
    last_name_xpath = "//input[@id='lastName']"
    email_xpath = "//input[@id='email']"

    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH, first_name_xpath)))

    driver.find_element_by_xpath(first_name_xpath).send_keys(first_name)
    driver.find_element_by_xpath(last_name_xpath).send_keys(last_name)
    driver.find_element_by_xpath(email_xpath).send_keys(email)

    driver.find_element_by_xpath("//input[@formcontrolname='contact']").send_keys(request_inputs)
    driver.find_element_by_xpath("//input[@formcontrolname='function']").send_keys(request_inputs)
    driver.find_element_by_xpath("//input[@formcontrolname='institution']").send_keys(request_inputs)
    driver.find_element_by_xpath("//input[@formcontrolname='service']").send_keys(request_inputs)
    driver.find_element_by_xpath("//input[@formcontrolname='study']").send_keys(request_inputs)
    driver.find_element_by_xpath("//input[@formcontrolname='work']").send_keys(request_inputs)
    time.sleep(1)

    # Submit
    submit_xpath = "//button[@type='submit']"
    wait_to_be_clickable_and_click(submit_xpath)

    # Click OK
    confirmation_xpath = "//label[contains (.,'An email has been sent to the administrator')]"
    wait_to_be_visible(confirmation_xpath)

    button_ok_xpath = "//button[contains(.,'OK')]"
    wait_to_be_clickable_and_click(button_ok_xpath)

    return email


def accept_deny_account_request(user, accept):
    # Check if is inactive
    span_od_xpath = "//tr[td[contains(.,'" + user + "')]]//span[contains(@class,'bool')]"
    is_waiting = driver.find_element_by_xpath(span_od_xpath).get_attribute('class')
    assert is_waiting == "bool-true"

    # Click on Edit button
    button_edit_xpath = "//tr[td[contains(.,'" + user + "')]]//a[contains(@href,'editUser')]"
    wait_to_be_present_and_click(button_edit_xpath)
    wait_to_be_visible("//label[contains(.,'Institution')]")

    if accept:
        # Choose role
        role = "Expert"
        wait_to_be_present_and_click("//select[@id='role']/option[contains(.,'"+role+"')]")
        wait_to_be_clickable_and_click("//button[@type='submit' and contains(.,'Accept')]")
    else:
        wait_to_be_clickable_and_click("//button[@type='submit' and contains(.,'Deny creation')]")


def add_center():
    wait_to_be_clickable_and_click("//shanoir-table//span[contains(.,'new center')]")

    assert wait_to_be_visible("//centerdetail//div[contains(.,'Create center')]")

    chu_name = "CHU Test "+str(random.randint(1000,9999))
    input_values = {"name": chu_name, "street": "1 Test Street", "postalCode": "35000", "city": "Rennes", \
                    "country": "France"}

    for key, value in input_values.iteritems():
        wait_and_send_keys("//input[@id='"+key+"']", value)

    wait_to_be_clickable_and_click("//button[@type='submit']")
    return chu_name


def view(name, service, editable=True):
    wait_to_be_present_and_click("//shanoir-table//tr[contains(.,'"+name+"')]")

    assert wait_to_be_visible("//centerdetail//div[contains(., 'Details on "+service+"')]")
    assert wait_to_be_visible("//centerdetail//li[label[contains(.,'Name')]]//span[contains(.,'"+name+"')]")

    len_button_edit = count_elements("//button[contains(.,'Edit')]")
    if editable:
        assert len_button_edit > 0
    else:
        assert len_button_edit == 0

    wait_to_be_clickable_and_click("//button[contains(.,'Back')]")
    assert check_if_shanoir_table_has_rows()


def edit_center(name):
    wait_to_be_present_and_click("//shanoir-table//tr[contains(.,'" + name + "')]//img[contains(@src, 'edit.png')]")
    assert wait_to_be_visible("//centerdetail//div[contains(., 'Edit center')]")

    input_values = {"phoneNumber": "0220111222", "website": "www.chutest.fr"}
    for key, value in input_values.iteritems():
        wait_and_send_keys("//input[@id='"+key+"']", value)

    wait_to_be_clickable_and_click("//button[contains(.,'Update')]")
    assert check_if_shanoir_table_has_rows()


def delete(name):
    wait_to_be_present_and_click("//shanoir-table//tr[contains(.,'" + name + "')]//img[contains(@src, 'garbage')]")
    assert wait_to_be_visible("//confirm-dialog[div[contains(.,'Are you sure you want to delete')]]")

    wait_to_be_clickable_and_click("//button[contains(.,'OK')]")
    assert check_if_shanoir_table_has_rows()


def add_manufacturer():
    new_man_model_button_xpath = "//button[contains(.,'new manufacturer model')]"
    wait_to_be_clickable_and_click(new_man_model_button_xpath)

    new_man_model_xpath = "//manufmodeldetail//div[contains(.,'Create manufacturer model')]"
    wait_to_be_visible(new_man_model_xpath)
    modality_option_xpath = "//select[@id='datasetModalityType']/option[@value='MR_DATASET']"
    wait_to_be_clickable_and_click(modality_option_xpath)

    # Create new manufacturer
    new_man_xpath = "//manufmodeldetail//button[contains(.,'new manufacturer')]"
    wait_to_be_clickable_and_click(new_man_xpath)
    manufacturer_xpath = "//manufdetail//div[contains(.,'Create manufacturer')]"
    assert wait_to_be_visible(manufacturer_xpath)

    manufacturer_name = "Testmanuf " + str(random.randint(1000, 9999))
    manufacturer_input_xpath = "//manufdetail//input[@id='name']"
    wait_and_send_keys(manufacturer_input_xpath, manufacturer_name)
    wait_to_be_clickable_and_click("//manufdetail//button[@type='submit']")

    model_name = "Testmodel "+ str(random.randint(1000, 9999))
    wait_and_send_keys("//input[@id='name']", model_name)
    wait_and_send_keys("//input[@id='magneticField']", '3.5')

    wait_to_be_clickable_and_click("//manufmodeldetail//button[@type='submit']")
    return (manufacturer_name, model_name)


def add_acq_eq(center, mode="add", man_model="", page="acq_eq"):
    if mode == "add":
        if page == "center":
            wait_to_be_clickable_and_click("//shanoir-table//span[contains(.,'new acq. equip.')]")
        elif page == "acq_eq":
            wait_to_be_clickable_and_click("//button[contains(.,'new acq. equip.')]")
        assert wait_to_be_visible("//acqequipdetail//div[contains(.,'Create acquisition equipment')]")
    elif mode == "edit":
        if page == "center":
            assert wait_to_be_visible("//list[@title='Acquisition Equipments List']")
            span_model_xpath = "//list[@title='Acquisition Equipments List']//span[contains(.,'"+man_model+"')]"
            wait_to_be_clickable_and_click(span_model_xpath)
        elif page == "acq_eq":
            tr_testmodel_xpath = "//tr[td[contains(.,'"+center+"')] and td[contains(.,'"+man_model+"')]]//img[contains\
            (@src,'edit')]"
            assert wait_to_be_visible(tr_testmodel_xpath)
            wait_to_be_clickable_and_click(tr_testmodel_xpath)

    if man_model == "":
        (manufacturer_name, model_name) = add_manufacturer()
    else:
        wait_to_be_clickable_and_click("//select[@id='manufModel']/option[contains(.,'" + man_model + "')]")

    wait_to_be_clickable_and_click("//select[@id='center']/option[contains(.,'"+center+"')]")

    acq_eq_number = random.randint(100000, 999999)

    # Clear and send new serial number
    input_serial_xpath = "//input[@id='serialNumber']"
    assert clear_input(input_serial_xpath)
    wait_and_send_keys(input_serial_xpath, acq_eq_number)
    time.sleep(1)
    wait_to_be_clickable_and_click("//acqequipdetail//button[@type='submit']")

    print acq_eq_number
    return str(acq_eq_number)


def test_shanoir_ng_users(user, password):
    # Request 2 accounts
    acc1 = request_account()
    acc2 = request_account()

    login(user, password)
    manage_users()

    # Accept account request
    search(search_string=acc1, select_option='Email')
    accept_deny_account_request(user=acc1, accept=True)
    search(search_string=acc1, select_option='Email')
    edit_user(name=acc1)
    clean_search()

    # Deny account request
    search(search_string=acc2, select_option='Email')
    accept_deny_account_request(user=acc2, accept=False)

    # Create, edit and delete user
    email = add_user()
    search(search_string=email, select_option='Email')
    edit_user(name=email)
    search(search_string=email, select_option='Email')
    delete(name=email)
    clean_search()


def test_shanoir_ng_center():
    manage_centers()
    # Create
    chu = add_center()
    # View
    search(search_string=chu, select_option="Name")
    view(name=chu, service="center", editable=True)
    # Edit
    search(search_string=chu, select_option="Name")
    edit_center(name=chu)
    # # Delete (not implemented)
    # search(search_string=chu, select_option="Name")
    # delete(name=chu)
    return chu


def test_shanoir_ng_acq_eq(chu):
    # ACQ EQ
    manage_acq_eq()
    # Create
    acq_number = add_acq_eq(chu, man_model="Verio", mode="add", page="acq_eq")

    # Edit
    search(search_string=acq_number,select_option="Serial number")
    new_acq_number = add_acq_eq(chu, man_model="Verio", mode="edit", page="acq_eq")

    return new_acq_number

if __name__ == "__main__":
    start_selenium()

    if args.shanoir == "users":
        test_shanoir_ng_users(args.user, args.password)
    elif args.shanoir == "studies":
        login(args.user, args.password)
        # chu = test_shanoir_ng_center()
        acq_eq = test_shanoir_ng_acq_eq("CHU Test 3577")

    time.sleep(1)
    logout()
    driver.quit()

