import React, { useState, useEffect } from "react";
import {
  StyleSheet,
  Text,
  Button,
  View,
  SafeAreaView,
  ActivityIndicator,
  FlatList,
} from "react-native";

const heroesHost = "http://localhost:8080";

const getWalletURL = heroesHost + "/pools/coro/accounts";
const postTransferURL = heroesHost + "/transfers";

const App = () => {
  const [isLoading, setLoading] = useState(true);
  const [user, setUser] = useState("bb");
  const [transfers, setTransfers] = useState([]);
  const [accountName, setAccountName] = useState([]);
  const [balance, setBalance] = useState([]);

  // gets called once
  useEffect(() => {
    getWalletAsync();
  }, []);

  async function postTransferAsync() {
    try {
      let response = await fetch(postTransferURL, {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          poolId: 'coro',
          from: 'aa',
          to: 'bb',
          amount: 5.55
        })
      });
    } catch (error) {
      alert(error);
    }
  }

  async function getWalletAsync() {
    try {
      let response = await fetch(getWalletURL + "/" + user + "/wallet", {
        method: 'GET',
        headers: {
          accept: 'application/json',
        },
      });
  
      let json = await response.json();
      setTransfers(json.account.transfers);
      setAccountName(json.account.poolId + '.' + json.account.accountId);
      setBalance(json.balance);
      setLoading(false);
    } catch (error) {
      alert(error);
    }
  }

  return (
    <SafeAreaView style={styles.container}>
      {/* While fetching show the indicator, else show response*/}
      {isLoading ? (
        <ActivityIndicator />
      ) : (
          <View>
          <Button
            onPress={() => {
              postTransferAsync();
            }}
            title={"send"}
          />
          <Button
            onPress={() => {
              setUser(user === "bb" ? "aa" : "bb");
              getWalletAsync();
              setLoading(true);
            }}
            title={user === "bb" ? "to aa" : "to bb"}
          />
          <Text style={styles.title}>Welcome '{accountName}' - your balance is: {balance}</Text>
          <View style={{ borderBottomWidth: 1, marginBottom: 12 }}></View>
          <FlatList
            data={transfers}
            keyExtractor={({ transferId }, index) => transferId}
            renderItem={({ item }) => (
              <View style={{ paddingBottom: 10 }}>
                <Text style={styles.transferText}>
                  id: {item.transferId} | from: {item.from} | to: {item.to} | amount: {item.amount}
                </Text>
              </View>
            )}
          />
        </View>
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    marginTop: 48,
  },
  transferText: {
    fontSize: 26,
    fontWeight: "200",
  },
  title: {
    fontSize: 32,
    fontWeight: "bold",
  },
});

export default App;