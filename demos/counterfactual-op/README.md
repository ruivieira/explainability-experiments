# counterfactual-op

REST server to generate counterfactuals for a credit card approval predict model.

## Usage

Before using this server, you might need to install the counterfactual search engine available on the [TrustyAI sandbox](https://github.com/kiegroup/trusty-ai-sandbox).
Simply clone the repository and run

```
$ cd trustyai-sandbox/explainability/core/counterfactuals
$ mvn clean install
```

Then, for this repository, build and start the counterfactual REST server:

```
$ mvn clean package
$ java -jar target/counterfactual-op-1.0.0-SNAPSHOT-runner.jar
```

### Predict

Predict a credit card approval with:

```json
curl --request POST \
  --url http://0.0.0.0:8080/creditcard/prediction \
  --header 'Content-Type: application/json' \
  --data '{
		"age": {
			"value": 20
		},
		"income": {
			"value": 50000
		},
		"children": {
			"value": 0
		},
		"daysEmployed": {
			"value": 100
		},
		"ownRealty": {
			"value": false
		},
		"workPhone": {
			"value": true
		},
		"ownCar": {
			"value": true
		}
}'
```

### Counterfactual

Calculate the counterfactual using:

```json
curl --request POST \
  --url http://0.0.0.0:8080/creditcard/counterfactual \
  --header 'Content-Type: application/json' \
  --data '{
	"input": {
		"age": {
			"value": 20,
			"constrain": true,
			"min": 18,
			"max": 100
		},
		"income": {
			"value": 50000,
			"min": 0,
			"max": 1000000
		},
		"children": {
			"value": 0,
			"constrain": true,
			"min": 0,
			"max": 20
		},
		"daysEmployed": {
			"value": 100,
			"min": 0,
			"max": 10000
		},
		"ownRealty": {
			"value": false
		},
		"workPhone": {
			"value": true
		},
		"ownCar": {
			"value": true
		}
	},
	"goal": {
		"output": {
			"approved": true,
			"confidence": 0.0
		}
	}
}'
```

The minimum confidence level can be customised using:

```json
"goal": {
    "output": {
        "approved": true,
        "confidence": 0.7
    }
}
```